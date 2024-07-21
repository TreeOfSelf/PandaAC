package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleHoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {

        Entity vehicle = serverPlayerEntity.getVehicle();
        if (vehicle == null) return false;

        boolean flagged = false;
        if (vehicleData.getChanged()) {
            if (!BlockUtil.checkGroundVehicleThicc(vehicle)) {
                if (vehicleData.getLastY() == vehicleData.getY()) {
                    if (vehicleData.getHover()) {
                        CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                        flagged = true;
                    }
                    vehicleData.setHover(true);
                } else {
                    vehicleData.setHover(false);
                }
            }
        }
        return flagged;
    }
}
