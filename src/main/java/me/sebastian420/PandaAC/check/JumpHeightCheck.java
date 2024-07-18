package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import net.minecraft.server.network.ServerPlayerEntity;

public class JumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        boolean flagged = false;
        if (playerData.getChanged()) {
            if (playerData.getY() - playerData.getLastAttachedY() > JumpHeights.NORMAL * JumpHeights.FUDGE &&
            playerData.getY() > playerData.getLastY()) {
                serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
                playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
                flagged = true;
            }
        }
        return flagged;
    }
}
