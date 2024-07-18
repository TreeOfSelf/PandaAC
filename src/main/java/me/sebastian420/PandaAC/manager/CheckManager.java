package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.check.*;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.server.network.ServerPlayerEntity;

public class CheckManager {

    public static void run(ServerPlayerEntity serverPlayerEntity, long time) {

        boolean running = true;

        while (running) {
            PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(serverPlayerEntity);

            if (serverPlayerEntity.isDisconnected()) break;
            if (HoverCheck.check(serverPlayerEntity, playerData)) {
                PandaLogger.getLogger().warn("Flagged Hover");
                playerData.moveCurrentToLast(time);
                break;
            }

            if (serverPlayerEntity.isDisconnected()) break;
            if (HorizontalSpeedCheck.check(serverPlayerEntity, playerData, time)) {
                PandaLogger.getLogger().warn("Flagged Horizontal Speed");
                playerData.moveCurrentToLast(time);
                break;
            }

            if (serverPlayerEntity.isDisconnected()) break;
            if (JumpHeightCheck.check(serverPlayerEntity, playerData)) {
                PandaLogger.getLogger().warn("Flagged Jump Height");
                playerData.moveCurrentToLast(time);
                break;
            }

            if (serverPlayerEntity.isDisconnected()) break;
            if (VerticalSpeedCheckUp.check(serverPlayerEntity, playerData, time)) {
                PandaLogger.getLogger().warn("Flagged Speed Check Up");
                playerData.moveCurrentToLast(time);
                break;
            }

            if (serverPlayerEntity.isDisconnected()) break;
            if (VerticalSpeedCheckDown.check(serverPlayerEntity, playerData, time)) {
                PandaLogger.getLogger().warn("Flagged Speed Check Down");
                playerData.moveCurrentToLast(time);
                break;
            }

            playerData.moveCurrentToLast(time);
            running = false;
        }
    }

    public static void rollBack(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData){
        serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
        playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
    }
}
