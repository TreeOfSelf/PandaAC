package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.check.player.*;
import me.sebastian420.PandaAC.check.player.fluid.FluidHorizontalSpeedCheck;
import me.sebastian420.PandaAC.check.player.fluid.FluidVerticalSpeedCheck;
import me.sebastian420.PandaAC.check.vehicle.*;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class CheckManager {

    public static void run(ServerPlayerEntity serverPlayerEntity, long time) {
        if (serverPlayerEntity.isCreative()) return;


        boolean running = true;
        while (running) {

            //Not in vehicle checks
            if (!serverPlayerEntity.hasVehicle()) {

                PlayerMovementData playerData = MovementManager.getPlayer(serverPlayerEntity);
                BlockPos lastBlockPos = new BlockPos((int) Math.floor(playerData.getLastX()),
                        (int) Math.floor(playerData.getLastY()),
                        (int) Math.floor(playerData.getLastZ()));

                BlockState lastBlockState = PandaACThread.fasterWorldManager.getWorld(serverPlayerEntity.getServerWorld()).getBlockState(lastBlockPos);
                //Fluid checks
                if (lastBlockState.getFluidState().isIn(FluidTags.WATER) ||
                        lastBlockState.getFluidState().isIn(FluidTags.LAVA)) {

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (FluidHorizontalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Horizontal Water Speed");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (FluidVerticalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Vertical Water Speed");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    // Out of water checks
                } else {
                    if (serverPlayerEntity.isDisconnected()) break;
                    if (HoverCheck.check(serverPlayerEntity, playerData)) {
                        PandaLogger.getLogger().warn("Flagged Hover");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (HorizontalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Horizontal Speed");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (JumpHeightCheck.check(serverPlayerEntity, playerData)) {
                        PandaLogger.getLogger().warn("Flagged Jump Height");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (VerticalSpeedCheckUp.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Vertical Speed Check");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (VerticalSpeedCheckDown.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Speed Check Down");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                }
                playerData.moveCurrentToLast(time);
                running = false;

            // In Vehicle checks
            } else {
                VehicleMovementData vehicleData = VehicleMovementManager.getPlayer(serverPlayerEntity);

                if (serverPlayerEntity.isDisconnected()) break;
                if (VehicleHorizontalSpeedCheck.check(serverPlayerEntity, vehicleData, time)) {
                    PandaLogger.getLogger().warn("Flagged Vehicle Horizontal Speed Check");
                    vehicleData.moveCurrentToLast(time);
                    break;
                }

                if (serverPlayerEntity.isDisconnected()) break;
                if (VehicleYawCheck.check(serverPlayerEntity, vehicleData, time)) {
                    PandaLogger.getLogger().warn("Flagged Vehicle Yaw Check");
                    vehicleData.moveCurrentToLast(time);
                    break;
                }

                if (serverPlayerEntity.isDisconnected()) break;
                if (VehicleHoverCheck.check(serverPlayerEntity, vehicleData)) {
                    PandaLogger.getLogger().warn("Flagged Vehicle Hover Check");
                    vehicleData.moveCurrentToLast(time);
                    break;
                }

                if (serverPlayerEntity.isDisconnected()) break;
                if (VehicleJumpHeightCheck.check(serverPlayerEntity, vehicleData)) {
                    PandaLogger.getLogger().warn("Flagged Vehicle Jump Height Check");
                    vehicleData.moveCurrentToLast(time);
                    break;
                }

                if (serverPlayerEntity.isDisconnected()) break;
                if (VehicleVerticalSpeedCheckDown.check(serverPlayerEntity, vehicleData, time)) {
                    PandaLogger.getLogger().warn("Flagged Vehicle Vertical Speed Down Check");
                    vehicleData.moveCurrentToLast(time);
                    break;
                }


                vehicleData.moveCurrentToLast(time);
                running = false;
            }
        }
    }

    public static void rollBack(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData){
        long time = System.currentTimeMillis();
        //We should do something like this where your velocity is counted til you hit the ground
        //Vec3d velocity = serverPlayerEntity.getVelocity();
        serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
        playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), time);
        //serverPlayerEntity.setVelocity(velocity);
    }

    public static void rollBackVehicle(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {
        long time = System.currentTimeMillis();

        Entity vehicle = serverPlayerEntity.getVehicle();
        if (vehicle == null) return;

        List<Entity> passengers = vehicle.getPassengerList();
        passengers.iterator().forEachRemaining(Entity::dismountVehicle);
        vehicle.teleport((ServerWorld) vehicle.getWorld(), vehicleData.getLastX(), vehicleData.getLastY(), vehicleData.getLastZ(), PositionFlag.VALUES, vehicle.getYaw(), vehicle.getPitch());
        vehicleData.teleport(vehicleData.getLastX(), vehicleData.getLastY(), vehicleData.getLastZ(), time);

        vehicle.setVelocity(new Vec3d(0,0,0));
    }
}
