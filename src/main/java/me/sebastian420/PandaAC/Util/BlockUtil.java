package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockUtil {

    private static BlockState checkVicinityGround(FasterWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos pos = new BlockPos(x + xx, y, z + zz);
                BlockState state = world.getBlockState(pos);
                BlockPos onTopPos = pos.offset(Direction.UP,1);
                BlockState stateTop = world.getBlockState(onTopPos);

                if (!state.getCollisionShape(world.realWorld,pos).isEmpty() &&
                        stateTop.getCollisionShape(world.realWorld,onTopPos).isEmpty()){
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

        BlockState blockBelow = checkVicinityGround(world, x, y - 1, z);

        return blockBelow != null;
    }
}
