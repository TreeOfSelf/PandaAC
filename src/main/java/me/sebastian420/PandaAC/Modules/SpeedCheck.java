package me.sebastian420.PandaAC.Modules;

import me.sebastian420.PandaAC.Data.SpeedLimits;
import me.sebastian420.PandaAC.Objects.Data.PlayerMovementData;
import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.Util.MathUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpeedCheck {
    public static void check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        if (playerData.getChanged()){
            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastX(), playerData.getLastZ(), playerData.getX(), playerData.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;

            if (speedMps > SpeedLimits.SPRINT_AND_JUMP * SpeedLimits.FUDGE) {
                if (playerData.getAverageSpeed() > SpeedLimits.SPRINT_AND_JUMP * SpeedLimits.FUDGE) {
                    serverPlayerEntity.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), false);
                    playerData.rollBack();
                    PandaAC.LOGGER.info("Rollback: "+playerData.getAverageSpeed());
                }
            }

            /*PandaAC.LOGGER.info("Raw values - Distance: {} blocks, Time: {} ms", distance, timeDifMs);
            PandaAC.LOGGER.info("Last {} Current {}",playerData.getLastSpeed(), speedMps);
            PandaAC.LOGGER.info("Moving at {} m/s",realSpeed);*/
        }
    }
}
