package me.sebastian420.PandaAC.objects.entity;

import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import me.sebastian420.PandaAC.util.BoxUtil;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface PAPlayer extends PAEntity {


    default boolean checkForSolidBlocksAroundPlayer(ServerPlayerEntity player) {
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();

        for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos currentPos = playerPos.add(x, y, z);
                    BlockState state = world.getBlockState(currentPos);
                    if (!state.isAir()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    default void _init() {
        PAEntity.super._init();
        PAPlayerEx ex = new PAPlayerEx(this);
        putData(PAPlayerEx.class, ex);
    }

    default void flag(int amount) {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        if (ex.flags > 0) {
            long timeDelta = System.currentTimeMillis() - ex.lastFlag;
            while (timeDelta > 1000) { //Runs once per second
                timeDelta -= 1000;
                //Max of 16 minor flags or 4 major per 1 min 
                ex.flags -= 1.0 / (16.0 * 5.0 * 60.0);
                if (ex.flags < 0) {
                    ex.flags = 0;
                }
            }
        }
        ex.flags += amount;
        ex.lastFlag = System.currentTimeMillis();
        if (ex.flags > 2) {
            //this.rollbackAndGround();

            //ServerPlayerEntity player = this.asMcPlayer();

            /*if(!checkForSolidBlocksAroundPlayer(player))*/ this.asMcPlayer().kill();

            ex.flags = 0;
            //kick(Text.literal("Flagged Too Much by AC"));
        }
    }

    default void minorFlag() {
        flag(1);
    }

    default void majorFlag() {
        flag(4);
    }

    /**
     * Call on failed Check
     * @return punish if true
     */
    default boolean flag(PAModule check, PAModule.FlagSeverity severity) {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        if (check.getFlagCoolDownMs() > System.currentTimeMillis() - ex.lastFlagsMap.getLong(check)) {
            return false;
        }
        ex.lastFlagsMap.put(check, System.currentTimeMillis());
        switch (severity) {
            case MAJOR:
                majorFlag();
            break;
            case MINOR:
                minorFlag();
            break;
        }
        return true;
    }

    @Nullable
    default ScreenHandler getCurrentScreenHandler() {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        return asMcPlayer().currentScreenHandler == asMcPlayer().playerScreenHandler && !ex.hasCurrentPlayerScreenHandler ? null : asMcPlayer().currentScreenHandler;
    }

    default void setHasCurrentPlayerScreenHandler(boolean hasCurrentPlayerScreenHandler) {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        ex.hasCurrentPlayerScreenHandler = hasCurrentPlayerScreenHandler;
    }

    default void rollback() {
        /*if(this.asMcPlayer().isAlive()) {
            PAPlayerEx ex = getData(PAPlayerEx.class);
            if (ex.hasLastGood) {
                teleportCd(ex.lastGoodX, ex.lastGoodY, ex.lastGoodZ);
                LoggerThread.info(String.format("Rolled %s back to %f %f %f", asString(), ex.lastGoodX, ex.lastGoodY, ex.lastGoodZ));
            }
        }*/
    }

    default void tickRollback(double x, double y, double z, boolean isTeleport) {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        if (System.currentTimeMillis() - ex.lastFlag > 5000 || isTeleport || !ex.hasLastGood) {
            // Never let last good coordinates be in midair
            if (isOnGround() || !ex.hasLastGood) {
                ex.lastGoodX = x;
                ex.lastGoodY = y;
                ex.lastGoodZ = z;
                ex.hasLastGood = true;
            }
        }
    }

    // TODO: Leaving this here for now to see if it's a good implementation
    default void rollbackAndGround() {
//        PAPlayerEx ex = getData(PAPlayerEx.class);
//        Box box = BoxUtil.withNewMinY(this.getBoxForPosition(ex.lastGoodX, this.getY(), ex.lastGoodZ), 0);
//        World world = this.getWorld();
//        // Calculate newY because lastGoodY might be in the air
//        double newY = BlockPos.stream(box).mapToDouble(pos -> BlockCollisionUtil.getHighestTopIntersection(world.getBlockState(pos).getCollisionShape(world, pos).offset(pos.getX(), pos.getY(), pos.getZ()), box, -100)).max().orElse(-100);
//        // Deal fall damage - Is this how you're supposed to do this?
//        asMcPlayer().fallDistance = (float) (this.getPacketY() - newY);
//        asMcPlayer().handleFall(0, true);
//        this.teleportCd(ex.lastGoodX, newY, ex.lastGoodZ);
        rollback();
    }

    default void groundBoat(PAEntity entity) {
        Box box = BoxUtil.withNewMinY(entity.getBox(), 0);
        World world = entity.getWorld();
        double newY = BlockPos.stream(box).mapToDouble(
                pos -> {
                    BlockState state = world.getBlockState(pos);
                    if (!state.getFluidState().isEmpty()) return pos.getY() + 1;
                    return BlockCollisionUtil.getHighestTopIntersection(state.getCollisionShape(world, pos).offset(pos.getX(), pos.getY(), pos.getZ()), box, -100);
                }
        ).max().orElse(-100);
        entity.asMcEntity().setPos(entity.getX(), newY, entity.getZ());
        this.getNetworkHandler().sendPacket(new VehicleMoveS2CPacket(entity.asMcEntity()));
    }


    default void teleportCd(double x, double y, double z) {
        teleportCd(x, y, z, getYaw(), getPitch());
        
        PAPlayerEx ex = getData(PAPlayerEx.class);
        ex.lastGoodX = x;
        ex.lastGoodY = y;
        ex.lastGoodZ = z;
        ex.hasLastGood = true;
    }

    //TODO: Still breaks boats somehow
    default void teleportCd(double x, double y, double z, float yaw, float pitch) {
        //asMcPlayer().stopRiding();
        asMcPlayer().teleport(getWorld(), x, y, z, yaw, pitch);
    }

    default void setPacketPos(PlayerMoveC2SPacketView packet) {
        PAPlayerEx ex = getData(PAPlayerEx.class);
        if (packet.isChangePosition()) {
            ex.lastPacketX = packet.getX();
            ex.lastPacketY = packet.getY();
            ex.lastPacketZ = packet.getZ();
        }
        if (packet.isChangeLook()) {
            ex.lastPacketYaw = packet.getYaw();
            ex.lastPacketPitch = packet.getPitch();
        }
    }

    default double getPacketX() {
        return getData(PAPlayerEx.class).lastPacketX;
    }

    default double getPacketY() {
        return getData(PAPlayerEx.class).lastPacketY;
    }

    default double getPacketZ() {
        return getData(PAPlayerEx.class).lastPacketZ;
    }

    default float getPacketYaw() {
        return getData(PAPlayerEx.class).lastPacketYaw;
    }

    default float getPacketPitch() {
        return getData(PAPlayerEx.class).lastPacketPitch;
    }

    /**
     * True if flying with an Elytra or similar
     */
    default boolean isFallFlying() {
        return asMcPlayer().isFallFlying();
    }

    default ServerPlayNetworkHandler getNetworkHandler() {
        return asMcPlayer().networkHandler;
    }


    default boolean isCreative() {
        return asMcPlayer().isCreative();
    }

    default boolean isSpectator() {
        return asMcPlayer().isSpectator();
    }

    default String asString() {
        return String.format(Locale.ROOT, "Player['%s'/%s, w='%s', x=%.2f, y=%.2f, z=%.2f]", asMcPlayer().getName().getString(), this.getUuid().toString(), this.getWorld() == null ? "~NULL~" : this.getWorld().getRegistryKey().getValue().toString(), this.getX(), this.getY(), this.getZ());
    }

    default ServerPlayerEntity asMcPlayer() {
        return (ServerPlayerEntity)this;
    }

    public static PAPlayer of(ServerPlayerEntity mcPlayer) {
        return ((PAPlayer)mcPlayer);
    }

    //GlideCheck.GlideCheckData getData(Class<GlideCheck.GlideCheckData> glideCheckDataClass, Object aNew);
}
