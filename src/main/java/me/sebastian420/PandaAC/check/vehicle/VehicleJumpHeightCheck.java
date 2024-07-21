package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleJumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {
        boolean flagged = false;
        if (vehicleData.getChanged()) {

            Entity vehicle = serverPlayerEntity.getVehicle();
            if (vehicle == null) return false;
            EntityType<?> type = vehicle.getType();

            double checkHeight = 0;


            if (vehicleData.getY() - vehicleData.getLastAttachedY() > checkHeight * JumpHeights.FUDGE &&
                    vehicleData.getY() > vehicleData.getLastY()) {
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                flagged = true;
            }
        }
        return flagged;
    }
}
