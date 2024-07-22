package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleVerticalSpeedCheckDown {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData, long time) {
        boolean flagged = false;

        //Make this based on the last time they had an attachment

        //If we have moved down since last
        if (vehicleData.getChanged() && vehicleData.getY() < vehicleData.getLastY()
                //If we are not currently attached (falling)
                && vehicleData.getLastAttachedY() != vehicleData.getY()) {

            long airTimeDif = time - vehicleData.getAirTimeStartTime();

            long timeDifMs = time - vehicleData.getLastCheck();
            double distance = MathUtil.getDistance(vehicleData.getLastY(), vehicleData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;
            double speedPotential = vehicleData.getVerticalSpeedPotential((double) timeDifMs / 1000d);



            if ( speedMps < speedPotential && airTimeDif > 500) {
                PandaLogger.getLogger().info("SPEED DOWN INFO speedMps {} Potential {}", speedMps, speedPotential);
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                flagged = true;
            }

        }
        return flagged;
    }
}
