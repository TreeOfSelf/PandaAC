package me.sebastian420.PandaAC.check.player.elytra;

import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import net.minecraft.server.network.ServerPlayerEntity;

public class ElytraHoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        boolean flagged = false;

        if (playerData.getChanged()) {


        }
        return flagged;
    }
}
