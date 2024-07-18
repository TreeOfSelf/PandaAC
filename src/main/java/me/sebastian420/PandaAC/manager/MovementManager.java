package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
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

            if(!player.isSneaking()) {
                //If they have enough hunger assume they are sprinting
                if (player.getHungerManager().getFoodLevel() > 6) {
                    //If they are in a 2 block tall passage assume they are jumping
                    if (PacketUtil.checkPassage(fasterWorld, packetView)) {
                        speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE;
                    } else {
                        //Assume sprint and jumping
                        speedPotential = SpeedLimits.SPRINT_AND_JUMP;
                    }
                    //Walking
                } else {
                    speedPotential = SpeedLimits.WALKING;
                }
            } else {
                speedPotential = SpeedLimits.SNEAKING;
            }

            if(packetView.isOnGround() || PacketUtil.checkClimbable(fasterWorld, packetView)) {
                BlockState belowState = PacketUtil.checkBouncyBelow(fasterWorld, packetView);
                playerData.setLastAttached(packetView.getX(), packetView.getY(), packetView.getZ(), belowState, player.getVelocity().getY(), time);
            }

            playerData.setSpeedPotential(speedPotential);
            playerData.setNew(packetView, time);
        }
    }


    public static void receiveTeleport(ServerPlayerEntity player, PlayerPositionLookS2CPacket teleportData) {
        PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);
        playerData.teleport(teleportData.getX(), teleportData.getY(), teleportData.getZ(), System.currentTimeMillis());
    }
}