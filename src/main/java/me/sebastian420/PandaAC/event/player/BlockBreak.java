package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBreak {
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> !shouldCancelBreak(player, world, pos));
    }

    private static boolean shouldCancelBreak(PlayerEntity player , World world, BlockPos pos) {
        double dist = MathUtil.getDistance(player.getPos().getX(), player.getPos().getY(), player.getPos().getZ(),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        PandaLogger.getLogger().info("BREAK DISTANCE: {}", dist);
        if (dist > 30) {
            return true;
        }
        return false;
    }
}
