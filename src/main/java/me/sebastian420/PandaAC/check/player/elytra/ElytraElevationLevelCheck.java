package me.sebastian420.PandaAC.check.player.elytra;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraElevationLevelCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        if (playerData.getChanged()) {
            //If we are rocketing
            if (time - playerData.getElytraLastRocketTime() < 0) {
                if (playerData.getY() > playerData.getElytraMaxElevation() + 5) {
                    PandaLogger.getLogger().warn("playerY {} elytraMaxElvevation {}", playerData.getY(), playerData.getElytraMaxElevation());
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            //If we are not rocketing
            } else {
                if (playerData.getY() > playerData.getElytraElevation() + 5) {
                    PandaLogger.getLogger().warn("playerY {} elytraElvevation {}", playerData.getY(), playerData.getElytraElevation());
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            }

        }
        return flagged;
    }
}
