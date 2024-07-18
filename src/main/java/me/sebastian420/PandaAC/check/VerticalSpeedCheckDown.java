package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class VerticalSpeedCheckDown {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;

        //If we have moved down since last
        if (playerData.getChanged() && playerData.getY() < playerData.getLastY()
                //If we are not currently attached (falling)
                && playerData.getLastAttachedY() != playerData.getY()) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;

            if (speedMps < Math.abs(serverPlayerEntity.getVelocity().getY())) {
                serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
                playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
                flagged = true;
            }

        }
        return flagged;
    }
}
