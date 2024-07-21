package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class HoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        boolean flagged = false;
        if (playerData.getChanged()) {
            if (!BlockUtil.checkGroundThicc(serverPlayerEntity)) {
                if (playerData.getLastY() == playerData.getY()) {
                    if (playerData.getHover()) {
                        CheckManager.rollBack(serverPlayerEntity, playerData);
                        flagged = true;
                    }
                    playerData.setHover(true);
                } else {
                    playerData.setHover(false);
                }
            }
        }
        return flagged;
    }
}
