package me.sebastian420.PandaAC.Modules;

import me.sebastian420.PandaAC.Objects.Threaded.FasterWorld;
import me.sebastian420.PandaAC.View.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.PandaACThread;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class MovementModuleOld {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        long time = System.currentTimeMillis();
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            //PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);
            FasterWorld threadedWorld = PandaACThread.threadedWorldManager.getWorld(player.getServerWorld());

            long curTime = System.currentTimeMillis();
            int iterate = 10;

            while(iterate > 0) {
                for (int x = -10; x <= 10; x++) {
                    for (int y = -10; y <= 10; y++) {
                        for (int z = -10; z <= 10; z++) {
                            BlockPos pos = new BlockPos(player.getBlockPos().add(x, y, z));
                            BlockState blockCheck = player.getWorld().getChunk(5,5).getBlockState(pos);
                            if (blockCheck.getBlock() == Blocks.SLIME_BLOCK) {
                                System.out.println("WORLD FOUND SLIME BLOCK" + pos);
                            }
                        }
                    }
                }
                iterate--;
            }

            System.out.println("World Check took:" + (System.currentTimeMillis() - curTime));



            curTime = System.currentTimeMillis();
            iterate = 10;

            while(iterate > 0) {
                for (int x = -10; x <= 10; x++) {
                    for (int y = -10; y <= 10; y++) {
                        for (int z = -10; z <= 10; z++) {
                            BlockPos pos = new BlockPos(player.getBlockPos().add(x, y, z));
                            BlockState blockCheck = threadedWorld.getBlockState(pos);
                            if (blockCheck.getBlock() == Blocks.SLIME_BLOCK) {
                                System.out.println("THREADEDWORLD FOUND SLIME BLOCK" + pos);
                            }
                        }
                    }
                }
                iterate--;
            }

            System.out.println("ThreadedWorld Check took:" + (System.currentTimeMillis() - curTime));

            /*
            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getX(), playerData.getZ(), packetView.getX(), packetView.getZ());

            BlockState blockUnder = threadedWorld.getBlockState(new BlockPos(player.getBlockPos().offset(Direction.DOWN)));
            double speedMps = (distance * 1000.0) / timeDifMs;
            PandaAC.LOGGER.info("Raw values - Distance: {} blocks, Time: {} ms", distance, timeDifMs);
            PandaAC.LOGGER.info("Moving at speed: {} m/s", speedMps);
            PandaAC.LOGGER.info("Block Under: {}", blockUnder);
            playerData.setNew(packetView, time);*/
        }
    }
}
