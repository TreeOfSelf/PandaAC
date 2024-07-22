package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.UUID;

public class VehicleMovementManager {

    public static HashMap<UUID, VehicleMovementData> vehicleMovementMap = new HashMap<>();

    public static VehicleMovementData getPlayer(ServerPlayerEntity player) {
        Entity vehicle = player.getVehicle();
        return vehicleMovementMap.computeIfAbsent(player.getUuid(), uuid -> new VehicleMovementData(player, vehicle));
    }

    public static void read(ServerPlayerEntity player, VehicleMoveC2SPacket packet, long time) {

        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;

        VehicleMovementData vehicleData = getPlayer(player);

        EntityType<?> type = vehicle.getType();

        double speedPotential = 0;
        double verticalSpeedPotential = 0;
        double yawPotential = 0;

        boolean onGround = BlockUtil.checkGroundVehicle(vehicle, packet.getY());
        BlockState currentFluidState = BlockUtil.checkFluidVehicle(vehicle, packet.getY());

        if (onGround || currentFluidState != Blocks.AIR.getDefaultState()) {
            vehicleData.setLastAttached((int) packet.getX(), (int) packet.getY(), (int) packet.getZ(), time);
        }

        if (type == EntityType.BOAT) {

            boolean previousOnIce = vehicleData.getOnIce();

            FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld((ServerWorld) vehicle.getWorld());
            BlockState blockStateUnder = BlockUtil.checkVicinityBoat(fasterWorld, (int) packet.getX(), (int) packet.getY() - 1, (int) packet.getZ());

            vehicleData.setOnIce(false);

            if (onGround || blockStateUnder != Blocks.AIR.getDefaultState()) {
                if (blockStateUnder.getFluidState().isIn(FluidTags.WATER)) {
                    speedPotential = SpeedLimits.BOAT_WATER;
                    yawPotential = SpeedLimits.BOAT_YAW_WATER;
                } else if (blockStateUnder.isIn(BlockTags.ICE)) {
                    yawPotential = SpeedLimits.BOAT_YAW_ICE;

                    if (blockStateUnder.getBlock() == Blocks.BLUE_ICE) {
                        speedPotential = SpeedLimits.BOAT_BLUE_ICE;
                        vehicleData.setOnIce(true);
                    } else {
                        speedPotential = SpeedLimits.BOAT_ICE;
                        vehicleData.setOnIce(true);
                    }
                } else {
                    speedPotential = SpeedLimits.BOAT_LAND;
                    yawPotential = SpeedLimits.BOAT_YAW_LAND;
                }
            } else {
                speedPotential = SpeedLimits.BOAT_AIR;
                yawPotential = SpeedLimits.BOAT_YAW_LAND;
            }

            if (!vehicleData.getOnIce() && previousOnIce) {
                vehicleData.setStoredSpeed(MathUtil.getDistance(vehicleData.getX(), vehicleData.getZ(), packet.getX(), packet.getZ()) * 125);
            } else {
                if ((onGround || blockStateUnder != Blocks.AIR.getDefaultState()) && vehicleData.getStoredSpeed() > 0) {
                    vehicleData.setStoredSpeed(vehicleData.getStoredSpeed() * 0.75);
                }
            }

        }

        if (!onGround  && currentFluidState == Blocks.AIR.getDefaultState()) {

            if (packet.getY() > vehicleData.getLastY()) {
                CheckManager.rollBackVehicle(player, vehicleData);

            //Falling
            } else if (packet.getY() < vehicleData.getLastY()) {
                if (type == EntityType.BOAT) {
                    verticalSpeedPotential += Math.abs(vehicle.getMovement().getY());
                }
            }
        }

        vehicleData.setSpeedPotential(speedPotential);
        vehicleData.setVerticalSpeedPotential(verticalSpeedPotential);
        vehicleData.setYawPotential(yawPotential);
        vehicleData.setNew(packet, vehicle.getUuid());
    }

    public static void setData(ServerPlayerEntity player, VehicleMoveS2CPacket packet) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;

        VehicleMovementData vehicleData = getPlayer(player);
        vehicleData.consumePacket(packet);
    }
}
