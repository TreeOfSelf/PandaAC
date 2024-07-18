package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class PacketUtil {

    private static BlockState checkVicinity(FasterWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockState state = world.getBlockState(new BlockPos(x + xx, y, z + zz));
                if (state.getBlock() != Blocks.AIR) {
                    return state;
                }
            }
        }
        return null;
    }

    private static BlockState checkVicinityAbove(FasterWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockState state = world.getBlockState(new BlockPos(x + xx, y, z + zz));
                BlockState oneBelowState = world.getBlockState(new BlockPos(x + xx, y - 1, z + zz));
                if (state.getBlock() != Blocks.AIR && oneBelowState.getBlock() == Blocks.AIR) {
                    return state;
                }
            }
        }
        return null;
    }


    public static boolean checkPassage(FasterWorld world, PlayerMoveC2SPacketView packetView) {
        int x = (int) Math.round(packetView.getX());
        int y = (int) Math.round(packetView.getY());
        int z = (int) Math.round(packetView.getZ());

        BlockState blockAbove = checkVicinityAbove(world, x, y + 2, z);
        BlockState blockBelow = checkVicinity(world, x, y - 1, z);

        if (blockAbove != null && blockBelow != null) {
            return true;
        }

        return false;
    }


}