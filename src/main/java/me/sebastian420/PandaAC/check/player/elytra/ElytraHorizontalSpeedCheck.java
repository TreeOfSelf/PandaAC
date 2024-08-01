package me.sebastian420.PandaAC.check.player.elytra;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraHorizontalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (playerData.getChanged()) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastX(), playerData.getLastZ(), playerData.getX(), playerData.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            //We should do a better timer check it is tricky though. Like the player sends 7 packets when standing on a boat for some reason
            if (playerData.getPacketCount() <= 7) {
                playerData.setPossibleTimer(false);
            }

            double storedSpeed = playerData.getStoredSpeed();

            double speedPotential = playerData.getSpeedPotential((double) timeDifMs / 1000d);
            double totalPotential = speedPotential + storedSpeed;

            double newStoredSpeed = storedSpeed - speedMps;

            if (newStoredSpeed > 0) {
                playerData.setStoredSpeed(newStoredSpeed);
            } else {
                playerData.setStoredSpeed(0);
            }

            if (speedMps > totalPotential || playerData.getPossibleTimer()) {
                playerData.incrementSpeedFlagCount();
                if (playerData.getSpeedFlagCount() > 0) {
                    PandaLogger.getLogger().warn("Speed {} Potential {} Count {}", speedMps, totalPotential, playerData.getPacketCount());
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            } else {
                playerData.decrementSpeedFlagCount();
            }

            if (playerData.getPacketCount() > 7) {
                playerData.setPossibleTimer(true);
            }

        }
        return flagged;
    }
}