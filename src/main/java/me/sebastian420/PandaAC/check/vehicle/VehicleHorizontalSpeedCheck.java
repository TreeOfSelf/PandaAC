package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleHorizontalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (vehicleData.getChanged()) {

            long timeDifMs = time - vehicleData.getLastCheck();
            double distance = MathUtil.getDistance(vehicleData.getLastX(), vehicleData.getLastZ(), vehicleData.getX(), vehicleData.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            //We should do a better timer check it is tricky though. Like the player sends 7 packets when standing on a boat for some reason
            if (vehicleData.getPacketCount() <= 6) {
                vehicleData.setPossibleTimer(false);
            }

            double storedSpeed = vehicleData.getStoredSpeed();

            double speedPotential = (vehicleData.getSpeedPotential((double) timeDifMs / 1000d)) * SpeedLimits.FUDGE;
            vehicleData.setAverageSpeed(speedMps);
            double totalPotential = speedPotential + storedSpeed;
            double lastPotential = vehicleData.getLastSpeedPotential() + storedSpeed;

            double newStoredSpeed = storedSpeed - speedMps;

            if (newStoredSpeed > 0) {
                vehicleData.setStoredSpeed(newStoredSpeed);
            } else {
                vehicleData.setStoredSpeed(0);
            }

            double avgSpeed = vehicleData.getAverageSpeed();


            if ( (speedMps > totalPotential && speedMps > lastPotential && avgSpeed > totalPotential) || vehicleData.getPossibleTimer()) {
                vehicleData.incrementSpeedFlagCount();
                if (vehicleData.getSpeedFlagCount() > 4 || vehicleData.getPossibleTimer()) {
                    PandaLogger.getLogger().warn("Speed {} Potential {} Stored {} Count {}", speedMps, speedPotential, storedSpeed, vehicleData.getPacketCount());
                    CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                    flagged = true;
                }
            } else {
                vehicleData.decrementSpeedFlagCount();
            }

            if (vehicleData.getPacketCount() > 6) {
                vehicleData.setPossibleTimer(true);
            }

            vehicleData.setLastSpeedPotential(speedPotential);


        }
        return flagged;
    }
}