package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleHorizontalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;


        if (vehicleData.getChanged() && !BlockUtil.checkOtherEntityVehicle(serverPlayerEntity, vehicleData.getY())) {

            long timeDifMs = time - vehicleData.getLastCheck();
            double distance = MathUtil.getDistance(vehicleData.getLastX(), vehicleData.getLastZ(), vehicleData.getX(), vehicleData.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            if (vehicleData.getPacketCount() <= 6) {
                vehicleData.setPossibleTimer(false);
            }

            double storedSpeed = vehicleData.getStoredSpeed();

            double speedPotential = vehicleData.getSpeedPotential((double) timeDifMs / 1000d);
            double totalPotential = speedPotential + vehicleData.getCarriedPotential() + storedSpeed;

            double newStoredSpeed = storedSpeed - speedMps;

            if (newStoredSpeed > 0) {
                vehicleData.setStoredSpeed(newStoredSpeed);
            } else {
                vehicleData.setStoredSpeed(0);
            }

            if (speedMps > totalPotential || vehicleData.getPossibleTimer()) {
                PandaLogger.getLogger().warn("Speed {} Potential {} Count {}", speedMps, totalPotential, vehicleData.getPacketCount());
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                vehicleData.setCarriedPotential(0);
                flagged = true;
            } else {
                vehicleData.setCarriedPotential(speedPotential - speedMps);
            }

            if (vehicleData.getPacketCount() > 6) {
                vehicleData.setPossibleTimer(true);
            }

        }
        return flagged;
    }
}