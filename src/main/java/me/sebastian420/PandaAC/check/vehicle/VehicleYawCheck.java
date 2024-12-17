package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleYawCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;


        if (vehicleData.getChanged() && !BlockUtil.checkOtherEntityVehicle(serverPlayerEntity, vehicleData.getY())) {

            long timeDifMs = time - vehicleData.getLastCheck();
            double distance = MathUtil.getDistance(vehicleData.getLastYaw(), vehicleData.getYaw());
            double speedMps = (distance * 1000.0) / timeDifMs;

            double totalPotential = vehicleData.getYawPotential((double) timeDifMs / 1000d);

            if (speedMps > totalPotential) {
                PandaLogger.getLogger().warn("Yaw Speed {} Potential {} {}", speedMps, totalPotential, serverPlayerEntity.getPlayerListName());
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                flagged = true;
            }
        }
        return flagged;
    }
}
