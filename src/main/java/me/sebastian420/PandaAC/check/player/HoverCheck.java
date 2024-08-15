package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class HoverCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;
        StatusEffectInstance levitation = serverPlayerEntity.getStatusEffect(StatusEffects.LEVITATION);
        long airTimeDif = time - playerData.getAirTimeStartTime();


        if (playerData.getChanged() && levitation == null && airTimeDif > 2000) {
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
