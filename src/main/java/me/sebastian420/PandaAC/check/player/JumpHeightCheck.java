package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;

public class JumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;


        if (playerData.getChanged() && time - playerData.getLastLevitation() > 500L) {

            double checkHeight = JumpHeights.NORMAL;

            if (playerData.getLastAttachedState().isIn(BlockTags.BEDS)) {
                checkHeight = JumpHeights.NORMAL + JumpHeights.BED * playerData.getLastAttachedVelocity();
            } else if  (playerData.getLastAttachedState().getBlock() == Blocks.SLIME_BLOCK) {
                checkHeight = JumpHeights.NORMAL + JumpHeights.SLIME * playerData.getLastAttachedVelocity();
            }

            checkHeight += playerData.getStoredSpeedVertical();

            StatusEffectInstance jumpBoost = serverPlayerEntity.getStatusEffect(StatusEffects.JUMP_BOOST);
            if (jumpBoost != null) {
                checkHeight *= 1 + (double) jumpBoost.getAmplifier() * 1.5;
            }

            if(serverPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) checkHeight += 1;

            if (playerData.getY() - playerData.getLastAttachedY() > checkHeight * JumpHeights.FUDGE &&
            playerData.getY() > playerData.getLastY()) {
                PandaLogger.getLogger().info("Y dif {} check height {} {}", playerData.getY() - playerData.getLastAttachedY(), checkHeight * JumpHeights.FUDGE, serverPlayerEntity.getPlayerListName());
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }
        }
        return flagged;
    }
}
