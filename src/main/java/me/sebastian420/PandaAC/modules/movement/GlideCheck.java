package me.sebastian420.PandaAC.modules.movement;


import me.sebastian420.PandaAC.LoggerThread;
import me.sebastian420.PandaAC.events.PlayerDamageListener;
import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.events.PlayerSpawnListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.Trackers;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GlideCheck extends PAModule implements PlayerMovementListener, PlayerDamageListener, PlayerSpawnListener {


    public static boolean isInWater(Entity entity) {
        World world = entity.getWorld();
        BlockPos entityPos = entity.getBlockPos();
        return !world.getBlockState(entityPos).getFluidState().isEmpty();
    }

    public static boolean checkForBoatEntityUnderPlayer(ServerPlayerEntity player, PlayerMoveC2SPacketView packet) {
        BlockPos packetPos = new BlockPos((int) packet.getX(), (int) packet.getY(), (int) packet.getZ());
        for (Entity e : player.getWorld().getOtherEntities(player, Box.of(packetPos.add(-10,-10,-10).toCenterPos(), 20, 20, 20))) {
            if (e instanceof BoatEntity && player.distanceTo(e) < 15 && !e.hasPlayerRider()) {
                return true;
            }
        }
        return false;
    }

    public static boolean solidBlockCheck(Entity playerEntity, PlayerMoveC2SPacketView packet) {


        int x = (int) packet.getX();
        int y = (int) packet.getY();
        int z = (int) packet.getZ();

        World world = playerEntity.getWorld();
        // Check for solid blocks in a cube around the player
        for (int i = -2; i <= 2; i++) {
            for (int j = -4; j <= 0;j++) {
                for (int k = -2; k <= 2; k++) {
                    BlockPos blockType = new BlockPos(x + i, y + j, z + k);
                    BlockState blockState = world.getBlockState(blockType);
                    if (!blockState.isAir()) {
                        return true;
                    }
                }
            }
        }

        return false; // No solid blocks found
    }


    public static boolean ladderCheck(Entity playerEntity, PlayerMoveC2SPacketView packet) {


        int x = (int) packet.getX();
        int y = (int) packet.getY();
        int z = (int) packet.getZ();
        
        World world = playerEntity.getWorld();
        // Check for ladder around player
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1;j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos blockType = new BlockPos(x + i, y + j, z + k);
                    BlockState blockState = world.getBlockState(blockType);
                    if (blockState.getBlock() == Blocks.LADDER
                    || blockState.getBlock() == Blocks.VINE
                            || blockState.getBlock() == Blocks.SCAFFOLDING
                            || blockState.getBlock() == Blocks.CAVE_VINES
                            || blockState.getBlock() == Blocks.TWISTING_VINES
                            || blockState.getBlock() == Blocks.WEEPING_VINES) {
                        return true;
                    }
                }
            }
        }

        return false; // No ladder
    }



    public boolean blockUnder(Entity playerEntity) {
        World world = playerEntity.getEntityWorld();
        BlockPos blockPos = new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 1), (int) playerEntity.getZ());
        BlockState blockUnder = world.getBlockState(blockPos);
        return blockUnder != Blocks.AIR.getDefaultState();
    }

    public GlideCheck() {
        super("glide_check");
        PlayerMovementListener.EVENT.register(this);
        PlayerDamageListener.EVENT.register(this);
        PlayerSpawnListener.EVENT.register(this);
        PlayerSpawnListener.EVENT.register(this::onSpawn);
    }

    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        if (player.isCreative() || player.isSpectator()) return;
        GlideCheckData data = player.getOrCreateData(GlideCheckData.class, GlideCheckData::new);
        long curTime = System.currentTimeMillis();
        long timeSinceLastPacket = curTime - data.lastPacket;
        int violationIncrement = Math.max((int) timeSinceLastPacket/50,1);

        if (data.violations.get() == 0){
            violationIncrement = 1;
        }

        data.lastPacket = curTime;

        if(data.lastX ==0 && data.lastY==0 && data.lastZ == 0 && blockUnder(player.asMcEntity())){
            data.lastX = player.asMcPlayer().getX();
            data.lastY = player.asMcPlayer().getY();
            data.lastZ = player.asMcPlayer().getZ();
        }

        boolean onGround = packet.isOnGround();
        if(onGround){
            if(!solidBlockCheck(player.asMcEntity(),packet) && packet.isChangePosition()){
                onGround = false;
                player.asMcPlayer().setOnGround(false);
                LoggerThread.info("FAKED "+player.asMcPlayer().getName());
                data.fakeGround+=1;
                player.asMcPlayer().damage(player.asMcPlayer().getWorld().getDamageSources().generic(), data.fakeGround+2);

            }else{
                data.fakeGround-=1;
                if(data.fakeGround>0) data.fakeGround = 0;
            }
        }


        if (!onGround && packet.isChangePosition() && !player.isFallFlying() && !ladderCheck(player.asMcEntity(),packet)) {
            if (BlockCollisionUtil.isNearby(player, 5, 5, BlockCollisionUtil.BOUNCY)) {
                data.enableAfter = System.currentTimeMillis() + 10000;
            }

            if(isInWater(player.asMcEntity()))  data.lastWater = System.currentTimeMillis() + 5000;

            if (System.currentTimeMillis() > data.lastWater && /*data.isActive && System.currentTimeMillis() > data.enableAfter && */!checkForBoatEntityUnderPlayer(player.asMcPlayer(),packet)) {

                boolean failedCheck = player.getPacketY() - packet.getY() < 0.70;
                if (failedCheck) {
                    data.violations.addAndGet(violationIncrement);
                } else {
                    data.violations.decrementAndGet();
                }
                int violations = data.violations.get();

                if (violations >= 50) {
                    if( /* (System.currentTimeMillis() - player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport < 1000) && */System.currentTimeMillis() - data.startAfter  > 500 )
                    {
                        player.asMcPlayer().kill();
                        data.violations.set(0);
                        //fail(player, player.getPacketY() - packet.getY()+6);
                    }

                }else{
                    if(violations > 15) {
                        if (!(data.lastX == 0 && data.lastY == 0 && data.lastZ == 0)) {
                            LoggerThread.info("GLIDE ROLLBACK " + player.asMcPlayer().getName());
                            if (packet.getY() + 1 < data.lastY && !blockUnder(player.asMcEntity())) {
                                player.asMcPlayer().kill();
                            } else {
                                player.asMcPlayer().teleport(player.getWorld().toServerWorld(), data.lastX, data.lastY, data.lastZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                            }
                        }else{
                            player.asMcPlayer().kill();
                        }
                    }
                }
                if (violations < 0) data.violations.getAndAdd(-1 * violations);
            }

            data.isActive = true;
        }else{
            if(onGround && blockUnder(player.asMcEntity())){
                data.lastX = player.asMcPlayer().getX();
                data.lastY = player.asMcPlayer().getY();
                data.lastZ = player.asMcPlayer().getZ();
                data.violations.set(0);
            }
            if(player.isFallFlying()){
                data.enableAfter = System.currentTimeMillis() + 3000;
            }
            data.violations.set(0);
        }
    }

    private void fail(PAPlayer player, double failamount) {
        if (failamount > 0.3) {
            //flag(player, FlagSeverity.MINOR, "Failed Glide Check (Minor) " + failamount);
            if (flag(player, FlagSeverity.MAJOR, "Failed Glide Check " + failamount));
        } else {
            // if (flag(player, FlagSeverity.MAJOR, "Failed Glide Check " + failamount)) PunishUtil.groundPlayer(player);
            if (flag(player, FlagSeverity.MAJOR, "Failed Glide Check " + failamount)) ;
        }
    }

    @Override
    public long getFlagCoolDownMs() {
        return 0;
    }

    @Override
    public void onSpawn(PAPlayer player) {
        GlideCheckData data = player.getOrCreateData(GlideCheckData.class, GlideCheckData::new);
        if (blockUnder(player.asMcPlayer())) {
            data.lastX = player.asMcPlayer().getX();
            data.lastY = player.asMcPlayer().getY();
            data.lastZ = player.asMcPlayer().getZ();
        }
        data.startAfter = System.currentTimeMillis();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            if (blockUnder(player.asMcPlayer())) {
                data.lastX = player.asMcPlayer().getX();
                data.lastY = player.asMcPlayer().getY();
                data.lastZ = player.asMcPlayer().getZ();
            }
            data.startAfter = System.currentTimeMillis();
            executor.shutdown();
        }, 1500, TimeUnit.MILLISECONDS);
    }
    //Should avoid edge cases good enough for now
    @Override
    public void onPlayerDamage(PAPlayer player, DamageSource source, float amount) {
        /*GlideCheckData data = player.getOrCreateData(GlideCheckData.class, GlideCheckData::new);
        int violations = data.violations.get();
        if (violations > 0) {
            data.violations.decrementAndGet();
        }*/
    }

    public class GlideCheckData {
        AtomicInteger violations = new AtomicInteger(0);
        boolean isActive = false;
        public long enableAfter = 0;

        public double lastX,lastY,lastZ = 0;

        public long startAfter = System.currentTimeMillis();

        public long lastWater = System.currentTimeMillis();

        public long lastPacket = System.currentTimeMillis();

        public int fakeGround = 0;

    }

}