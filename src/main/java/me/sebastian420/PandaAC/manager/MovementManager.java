package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.UUID;

public class MovementManager {

    public static HashMap<UUID, PlayerMovementData> playerMovementMap = new HashMap<>();

    public static PlayerMovementData getPlayer(ServerPlayerEntity player) {
        return playerMovementMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerMovementData(player));
    }

    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet, long time) {
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            PlayerMovementData playerData = getPlayer(player);
            FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());

            double speedPotential;
            boolean inLiquid = false;

            BlockPos lastBlockPos = new BlockPos((int) Math.floor(playerData.getX()), (int) Math.floor(playerData.getY()), (int) Math.floor(playerData.getZ()));
            BlockState lastBlockState = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld()).getBlockState(lastBlockPos);

            if (lastBlockState.getBlock() == Blocks.WATER) {
                speedPotential = SpeedLimits.SWIM_SPEED_WATER;
                inLiquid = true;
            } else if (lastBlockState.getBlock() == Blocks.LAVA) {
                speedPotential = SpeedLimits.SWIM_SPEED_LAVA;
                inLiquid = true;
            } else if (!player.isSneaking()) {
                BlockState blockStateUnder = BlockUtil.checkVicinityIce(fasterWorld,(int) playerData.getX(), (int) playerData.getY(), (int) playerData.getZ());
                //If they have enough hunger assume they are sprinting
                if (player.getHungerManager().getFoodLevel() > 6) {
                    //If they are in a 2 block tall passage assume they are jumping
                    if (PacketUtil.checkPassage(fasterWorld, packetView)) {

                        if (blockStateUnder.isIn(BlockTags.ICE)) {
                            if (blockStateUnder.getBlock() == Blocks.BLUE_ICE) {
                                speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE_BLUE_ICE;
                            } else {
                                speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE_ICE;
                            }
                        } else {
                            speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE;
                        }
                    } else {
                        //Assume sprint and jumping
                        if (blockStateUnder.isIn(BlockTags.ICE)) {
                            if (blockStateUnder.getBlock() == Blocks.BLUE_ICE) {
                                speedPotential = SpeedLimits.SPRINT_ON_BLUE_ICE;
                            } else {
                                speedPotential = SpeedLimits.SPRINT_ON_ICE;
                            }
                        } else {
                            speedPotential = SpeedLimits.SPRINT_AND_JUMP;
                        }
                    }
                    //Walking
                } else {
                    speedPotential = SpeedLimits.WALKING;
                }
            } else {
                speedPotential = SpeedLimits.SNEAKING;
            }


            if( BlockUtil.checkGround(player, packetView.getY()) || PacketUtil.checkClimbable(fasterWorld, packetView)) {
                BlockState belowState = PacketUtil.checkBouncyBelow(fasterWorld, packetView);
                playerData.setLastAttached(packetView.getX(), packetView.getY(), packetView.getZ(), belowState, player.getVelocity().getY(), time);
            }else if (time - playerData.getLastSolidTouch() > 1000 &&
                    packetView.getY() > playerData.getLastY() && !inLiquid && time - playerData.getLastWaterTime() > 500) {
                CheckManager.rollBack(player ,playerData);
                return;
            } else if (inLiquid) {
                playerData.setLastAttachedLiquid(packetView.getX(), packetView.getY(), packetView.getZ(), time);
            }

            playerData.setSpeedPotential(speedPotential);
            playerData.setNew(packetView, time);
        }
    }


    public static void receiveTeleport(ServerPlayerEntity player, PlayerPositionLookS2CPacket teleportData) {
        PlayerMovementData playerData = getPlayer(player);
        playerData.teleport(teleportData.getX(), teleportData.getY(), teleportData.getZ(), System.currentTimeMillis());
    }
}