package me.sebastian420.PandaAC.check.player.fluid;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class FluidVerticalSpeedCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        //If the player has since had a movement packet
        boolean flagged = false;

        if (playerData.getChanged()) {

            BlockPos lastBlockPos = new BlockPos((int) Math.floor(playerData.getLastX()), (int) Math.floor(playerData.getLastY()), (int) Math.floor(playerData.getLastZ()));
            BlockState lastBlockState = PandaACThread.fasterWorldManager.getWorld(serverPlayerEntity.getServerWorld()).getBlockState(lastBlockPos);

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(
                    playerData.getLastY(),
                    playerData.getY());

            double speedMps = (distance * 1000.0) / timeDifMs;
            double speedPotential = playerData.getVerticalSpeedPotential((double) timeDifMs / 1000d);

            if (speedMps > speedPotential) {
                PandaLogger.getLogger().warn("Swim Vertical Speed {} Potential {}", speedMps, speedPotential);
                CheckManager.rollBack(serverPlayerEntity, playerData);
                playerData.setCarriedPotential(0);
            }



        }
        return flagged;
    }
}
