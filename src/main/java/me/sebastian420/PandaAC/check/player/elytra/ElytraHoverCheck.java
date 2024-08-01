package me.sebastian420.PandaAC.check.player.elytra;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraHoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        if (playerData.getChanged()) {
            double yDistance = Math.abs(playerData.getLastY() - playerData.getY());
            double horizontalDistance = MathUtil.getDistance(playerData.getLastX(), playerData.getLastZ(), playerData.getX(), playerData.getZ());


            if (yDistance < 0.2 && horizontalDistance < 3) {
                playerData.incrementElytraHoverCount();
                if (playerData.getElytraHoverCount() > 3) {
                    PandaLogger.getLogger().warn("yDistance {} horizontalDistance {}", yDistance, horizontalDistance);
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            } else {
                playerData.decrementElytraHoverCount();
            }
        }
        return flagged;
    }
}
