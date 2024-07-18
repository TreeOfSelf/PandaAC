package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class BlockUtil {

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

    public static boolean checkGround(ServerPlayerEntity serverPlayerEntity, PlayerMovementData playerData) {
        FasterWorld world = PandaACThread.fasterWorldManager.getWorld(serverPlayerEntity.getServerWorld());
        int x = (int) Math.round(playerData.getX());
        int y = (int) Math.round(playerData.getY());
        int z = (int) Math.round(playerData.getZ());

        BlockState blockBelow = checkVicinity(world, x, y - 1, z);

        return blockBelow != null;
    }
}
