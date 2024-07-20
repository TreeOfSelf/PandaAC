package me.sebastian420.PandaAC.check.player;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class VerticalSpeedCheckUp {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData, long time) {
        boolean flagged = false;



        //If we have moved up since last
        if (playerData.getChanged() && playerData.getY() > playerData.getLastY() &&
                time - playerData.getLastWaterTime() > 500) {

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getLastY(), playerData.getY());
            double speedMps = (distance * 1000.0) / timeDifMs;


            if (speedMps > SpeedLimits.UP_SPEED) {
                CheckManager.rollBack(serverPlayerEntity, playerData);
                flagged = true;
            }

        }
        return flagged;
    }
}
