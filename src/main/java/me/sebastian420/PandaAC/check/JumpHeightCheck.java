package me.sebastian420.PandaAC.check;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;

public class JumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        boolean flagged = false;
        if (playerData.getChanged()) {

            double checkHeight = JumpHeights.NORMAL;

            if (playerData.getLastAttachedState().isIn(BlockTags.BEDS)) {
                checkHeight = JumpHeights.NORMAL + JumpHeights.BED * playerData.getLastAttachedVelocity();
            } else if  (playerData.getLastAttachedState().getBlock() == Blocks.SLIME_BLOCK) {
                checkHeight = JumpHeights.NORMAL + JumpHeights.SLIME * playerData.getLastAttachedVelocity();
            }

            if (playerData.getY() - playerData.getLastAttachedY() > checkHeight * JumpHeights.FUDGE &&
            playerData.getY() > playerData.getLastY()) {

                PandaLogger.getLogger().info("Checkheight {} Velocity {}", checkHeight, playerData.getLastAttachedVelocity());

                serverPlayerEntity.teleport(serverPlayerEntity.getServerWorld(), playerData.getLastX(), playerData.getLastY(), playerData.getLastZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
                playerData.teleport(playerData.getLastX(), playerData.getLastY(), playerData.getLastZ());
                flagged = true;
            }
        }
        return flagged;
    }
}
