package me.sebastian420.PandaAC.trackers;

import me.sebastian420.PandaAC.events.*;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.trackers.data.PhaseBypassData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;

public class PhaseBypassTracker extends Tracker<PhaseBypassData> implements SetBlockStateListener, PlayerEndTickCallback, PlayerSpawnListener, PlayerStartRidingListener, TeleportConfirmListener {

    /**
     * True if not bypassed
     */
    public BiPredicate<World, BlockPos> isNotBypassed(PAPlayer player) {
        PhaseBypassData data = get(player);
        synchronized(data.bypassPos) {
            return (world, blockpos) -> data.bypassPos.indexOf(blockpos.asLong()) == -1;
        }
    }

    protected PhaseBypassTracker() {
        super(PhaseBypassData.class);
        SetBlockStateListener.EVENT.register(this);
        PlayerEndTickCallback.EVENT.register(this);
        PlayerStartRidingListener.EVENT.register(this);
        TeleportConfirmListener.EVENT.register(this);
    }

    @Override
    public @NotNull PhaseBypassData get(PAEntity entity) {
        return entity.getOrCreateData(PhaseBypassData.class, PhaseBypassData::new);
    }

    private static int getMaxBypassDistanceSquared(PAPlayer player) {
        return player.getVehicleCd() == null ? 64 : 25;
    }

    @Override
    public void onSetBlockState(ServerWorld world, BlockPos pos, BlockState state) {
        List<ServerPlayerEntity> players = world.getPlayers();
        for (ServerPlayerEntity player1 : players) {
            PAPlayer player = PAPlayer.of(player1);
            if (pos.getSquaredDistance(player.getX(), player.getY(), player.getZ()) < getMaxBypassDistanceSquared(player)) {
                PhaseBypassData data = get(player);
                synchronized(data.bypassPos) {
                    data.bypassPos.add(pos.asLong());
                }
            }
        }
    }

    private static LongPredicate distanceFilter(PAPlayer player) {
        return posLong -> {
            int x = BlockPos.unpackLongX(posLong);
            int y = BlockPos.unpackLongY(posLong);
            int z = BlockPos.unpackLongZ(posLong);
            return MathUtil.getDistanceSquared(x, y, z, player.getX(), player.getY(), player.getZ()) >= getMaxBypassDistanceSquared(player);
        };
    }

    @Override
    public void onPlayerEndTick(PAPlayer player) {
        PhaseBypassData data = get(player);
        if (System.currentTimeMillis() - data.lastUpdated > 1000) {
            //Do some clean up
            synchronized(data.bypassPos) {
                data.bypassPos.removeIf(distanceFilter(player));
            }
        }
    }

    @Override
    public void onSpawn(PAPlayer player) {
        PhaseBypassData data = get(player);
        data.lastUpdated = System.currentTimeMillis();
        synchronized(data.bypassPos) {
            BlockPos.stream(player.getBox().expand(-0.1)).forEach(pos -> data.bypassPos.add(pos.asLong()));
        }
    }

    @Override
    public void onStartRiding(PAPlayer player, PAEntity vehicle) {
        PhaseBypassData data = get(player);
        data.lastUpdated = System.currentTimeMillis();
        synchronized(data.bypassPos) {
            BlockPos.stream(vehicle.getBox().expand(-0.1)).forEach(pos -> data.bypassPos.add(pos.asLong()));
        }
    }

	@Override
	public void onTeleportConfirm(PAPlayer player, TeleportConfirmC2SPacket teleportConfirmC2SPacket, PlayerMoveC2SPacketView playerMoveC2SPacketView) {
		PhaseBypassData data = get(player);
        data.lastUpdated = System.currentTimeMillis();
        synchronized(data.bypassPos) {
            BlockPos.stream(player.getBoxForPosition(playerMoveC2SPacketView.getX(), playerMoveC2SPacketView.getY(), playerMoveC2SPacketView.getZ())).forEach(pos -> data.bypassPos.add(pos.asLong()));
        }
	}

}
