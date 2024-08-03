package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakBlock {
    public static void register(){
        PlayerBlockBreakEvents.BEFORE.register(BreakBlock::onBreakBlock);
    }

    private static boolean onBreakBlock(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        double distance = playerEntity.getCameraPosVec(1.0f).squaredDistanceTo(blockPos.toCenterPos());
        if (distance > 26.1) {
            PandaLogger.getLogger().info("Break distance failed {}", distance);
            return false;
        }
        return true;
    }

}
