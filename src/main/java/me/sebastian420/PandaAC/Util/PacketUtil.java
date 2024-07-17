package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PacketUtil {
    public static boolean checkPassage(FasterWorld world, PlayerMoveC2SPacketView packetView) {
        double x = packetView.getX();
        double y = packetView.getY();
        double z = packetView.getZ();

        double[][] offsets = {
                {0, 0},
                {-0.3, -0.3},
                {-0.3, 0.3},
                {0.3, -0.3},
                {0.3, 0.3}
        };

        // Check blocks above each offset
        for (double[] offset : offsets) {
            BlockPos pos = new BlockPos((int) (x + offset[0]), (int) y, (int) (z + offset[1]));
            for (int i = 1; i <= 2; i++) {
                BlockPos checkPos = pos.offset(Direction.UP, i);
                if (world.getBlockState(checkPos).getBlock() != Blocks.AIR) {
                    for (double[] belowOffset : offsets) {
                        BlockPos belowPos = new BlockPos((int) (x + belowOffset[0]), (int) y, (int) (z + belowOffset[1]));
                        BlockPos belowCheckPos = belowPos.offset(Direction.DOWN, 1);
                        if (world.getBlockState(belowCheckPos).getBlock() != Blocks.AIR) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


}