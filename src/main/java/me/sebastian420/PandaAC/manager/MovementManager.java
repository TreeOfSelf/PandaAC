package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class MovementManager {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        long time = System.currentTimeMillis();
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);
            FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());


            double speedPotential;

            if (player.isSprinting()) {
                if (playerData.getY() % 1 != 0) {
                    if (PacketUtil.checkPassage(fasterWorld, packetView)) {
                        speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE;
                        PandaLogger.getLogger().info("SPRINTING AND JUMPING IN PASSAGE");
                    } else {
                        speedPotential = SpeedLimits.SPRINT_AND_JUMP;
                    }
                } else {
                    speedPotential = SpeedLimits.SPRINT;
                }
            } else {
                speedPotential = SpeedLimits.WALKING;
            }


            long timeDifMs = time - playerData.getLastShortCheck();
            double distance = MathUtil.getDistance(playerData.getX(), playerData.getZ(), packetView.getX(), packetView.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            //Set intensity (the 0.3) based on how many you have received
            playerData.setSpeedPotential(speedPotential * 0.255);
            playerData.setNew(packetView, time);
        }
    }
}