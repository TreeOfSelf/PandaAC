package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class HoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        boolean flagged = false;
        if (playerData.getChanged()) {
            if (!BlockUtil.checkGround(serverPlayerEntity, playerData)) {
                if (playerData.getLastY() == playerData.getY()) {
                    serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
                    playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());

                    flagged = true;
                }
            }
        }
        return flagged;
    }
}
