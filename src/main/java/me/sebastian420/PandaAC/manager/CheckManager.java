package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.check.HorizontalSpeedCheck;
import me.sebastian420.PandaAC.check.HoverCheck;
import me.sebastian420.PandaAC.check.JumpHeightCheck;
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
                PandaLogger.getLogger().warn("Jump Height");
                playerData.moveCurrentToLast(time);
                break;
            }

            playerData.moveCurrentToLast(time);
            running = false;
        }
    }
}
