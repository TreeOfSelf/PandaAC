package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class VerticalSpeedCheckDown {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        //Make this based on the last time they had an attachment

        //If we have moved down since last

        if (playerData.getChanged() && playerData.getY() < playerData.getLastY() && time - playerData.getLastLevitation() > 500L
                //If we are not currently attached (falling)
                && playerData.getLastAttachedY() != playerData.getY()) {

            StatusEffectInstance levitation = serverPlayerEntity.getStatusEffect(StatusEffects.LEVITATION);

            if (levitation != null) {
                PandaLogger.getLogger().info("SPEED DOWN INFO WENT DOWN WHILE LEVITATING");
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
                return (flagged);
            }

            long airTimeDif = time - playerData.getAirTimeStartTime();

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;
            double speedPotential = playerData.getVerticalSpeedPotential((double) timeDifMs / 1000d);


            long solidBlockTimeDif = time - playerData.getLastSolidTouch();
            if ( (solidBlockTimeDif > 1000 && speedMps < speedPotential / 5 && airTimeDif > 500)) {
                PandaLogger.getLogger().info("SPEED DOWN INFO speedMps {} Potential {}", speedMps, speedPotential);
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }

        }
        return flagged;
    }
}
