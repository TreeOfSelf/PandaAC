package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleJumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {
        boolean flagged = false;
        if (vehicleData.getChanged()) {

            Entity vehicle = serverPlayerEntity.getVehicle();
            if (vehicle == null) return false;
            EntityType<?> type = vehicle.getType();
            double checkHeight = 1;

            if (type == EntityType.HORSE) {
                HorseEntity horseEntity = (HorseEntity) vehicle;
                checkHeight = horseEntity.getJumpBoostVelocityModifier();
            }

            if (vehicleData.getY() - vehicleData.getLastAttachedY() > checkHeight * JumpHeights.FUDGE &&
                    vehicleData.getY() > vehicleData.getLastY()) {
                PandaLogger.getLogger().info("Height dif {} Checkheight {}", vehicleData.getY() - vehicleData.getLastAttachedY(), checkHeight);
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                flagged = true;
            }
        }
        return flagged;
    }
}
