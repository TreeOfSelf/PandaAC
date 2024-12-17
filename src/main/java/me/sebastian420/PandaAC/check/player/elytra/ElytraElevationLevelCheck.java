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
                if (playerData.getY() > playerData.getElytraMaxElevation() + 15) {
                    PandaLogger.getLogger().warn("playerY {} elytraMaxElvevation rocketing {} {}", playerData.getY(), playerData.getElytraMaxElevation(), serverPlayerEntity.getPlayerListName());
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            //If we are not rocketing
            } else {
                if (playerData.getY() > playerData.getElytraElevation() + 15) {
                    PandaLogger.getLogger().warn("playerY {} elytraElvevation not-rocketing {} {}", playerData.getY(), playerData.getElytraElevation(), serverPlayerEntity.getPlayerListName());
                    CheckManager.rollBack(serverPlayerEntity, playerData);
                    flagged = true;
                }
            }

        }
        return flagged;
    }
}
