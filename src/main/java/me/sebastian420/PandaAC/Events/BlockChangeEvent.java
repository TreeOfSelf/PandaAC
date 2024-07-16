package me.sebastian420.PandaAC.Events;

import me.sebastian420.PandaAC.PandaACThread;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockChangeEvent {
    public static void change(World world, BlockPos blockPos, BlockState newState) {
        PandaACThread.queueBlockChange(world, blockPos, newState);
    }
}
