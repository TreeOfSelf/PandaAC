package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.check.player.*;
import me.sebastian420.PandaAC.check.player.elytra.ElytraElevationLevelCheck;
import me.sebastian420.PandaAC.check.player.elytra.ElytraHorizontalSpeedCheck;
import me.sebastian420.PandaAC.check.player.elytra.ElytraHoverCheck;
import me.sebastian420.PandaAC.check.player.elytra.ElytraVerticalSpeedCheck;
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
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.UUID;

public class CheckManager {

    public static void run(ServerPlayerEntity serverPlayerEntity, long time) {
        if (serverPlayerEntity.isCreative() || serverPlayerEntity.isSpectator()) return;

        boolean running = true;
        while (running) {

            //Not in vehicle checks
            if (!serverPlayerEntity.hasVehicle()) {

                PlayerMovementData playerData = MovementManager.getPlayer(serverPlayerEntity);
                BlockPos lastBlockPos = new BlockPos((int) Math.floor(playerData.getLastX()),
                        (int) Math.floor(playerData.getLastY()),
                        (int) Math.floor(playerData.getLastZ()));

                BlockState lastBlockState = serverPlayerEntity.getServerWorld().getBlockState(lastBlockPos);

                //Non-Elytra
                if (!serverPlayerEntity.isGliding()) {
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
                        if (HoverCheck.check(serverPlayerEntity, playerData, time)) {
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
                        if (JumpHeightCheck.check(serverPlayerEntity, playerData, time)) {
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
                } else {
                    //Elytra

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (ElytraHorizontalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Elytra Horizontal Speed");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    /*if (serverPlayerEntity.isDisconnected()) break;
                    if (ElytraHoverCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Elytra Hover");
                        playerData.moveCurrentToLast(time);
                        break;
                    }*/

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (ElytraVerticalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Elytra Vertical Speed");
                        playerData.moveCurrentToLast(time);
                        break;
                    }

                    if (serverPlayerEntity.isDisconnected()) break;
                    if (ElytraElevationLevelCheck.check(serverPlayerEntity, playerData, time)) {
                        PandaLogger.getLogger().warn("Flagged Elytra Elevation Level");
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
        if (!playerData.getStarted() || playerData.getServerWorld() != serverPlayerEntity.getServerWorld()) return;
        if (playerData.getLastX() == 0 && playerData.getLastY() == 0 && playerData.getLastZ() == 0) return;
        if (Double.isNaN(playerData.getLastX()) || Double.isNaN(playerData.getLastY()) || Double.isNaN(playerData.getLastZ())) return;

        long time = System.currentTimeMillis();
        //We should do something like this where your velocity is counted til you hit the ground
        //Vec3d velocity = serverPlayerEntity.getVelocity();

        serverPlayerEntity.teleport(playerData.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(),PositionFlag.DELTA , serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch(), false );
        playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), time);
        PandaLogger.getLogger().info("TELEPORTED TO {} {} {}", playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
        //serverPlayerEntity.setVelocity(velocity);
    }

    public static void rollBackVehicle(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {
        if (!vehicleData.getStarted() || vehicleData.getServerWorld() != serverPlayerEntity.getServerWorld()) return;
        if (vehicleData.getLastX() == 0 && vehicleData.getLastY() == 0 && vehicleData.getLastZ() == 0) return;
        if (Double.isNaN(vehicleData.getLastX()) || Double.isNaN(vehicleData.getLastY()) || Double.isNaN(vehicleData.getLastZ())) return;

        long time = System.currentTimeMillis();

        Entity vehicle = serverPlayerEntity.getVehicle();
        if (vehicle == null) return;

        List<Entity> passengers = vehicle.getPassengerList();
        passengers.iterator().forEachRemaining(Entity::dismountVehicle);
        vehicle.teleport(vehicleData.getServerWorld(), vehicleData.getLastX(), vehicleData.getLastY(), vehicleData.getLastZ(), PositionFlag.DELTA, vehicle.getYaw(), vehicle.getPitch(), false);
        vehicleData.teleport(vehicleData.getLastX(), vehicleData.getLastY(), vehicleData.getLastZ(), time);

        vehicle.setVelocity(new Vec3d(0,0,0));
    }
}
