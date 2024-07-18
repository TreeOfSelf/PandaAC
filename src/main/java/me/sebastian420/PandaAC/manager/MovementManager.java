package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public class MovementManager {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet, long time) {
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);
            FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());


            double speedPotential;

            PandaLogger.getLogger().info(fasterWorld.getBlockState(player.getBlockPos().offset(Direction.DOWN,1)));

            if (player.isSprinting() || true) {
                if (playerData.getY() % 1 != 0 || player.getVelocity().getY() > 0 || true) {
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


            playerData.setSpeedPotential(speedPotential);
            playerData.setNew(packetView, time);
        }
    }

    public static void teleport(ServerPlayerEntity player, PlayerPositionLookS2CPacket teleportData) {
        PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);
        playerData.teleport(teleportData.getX(), teleportData.getY(), teleportData.getZ());
    }
}