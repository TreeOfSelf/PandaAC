package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.manager.object.MovementPacketData;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PacketUtil {

    private static BlockState checkVicinityGround(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos pos = new BlockPos(x + xx, y, z + zz);
                BlockState state = world.getBlockState(pos);
                BlockPos onTopPos = pos.offset(Direction.UP,1);
                BlockState stateTop = world.getBlockState(onTopPos);

                if (!state.getCollisionShape(world,pos).isEmpty() &&
                        stateTop.getCollisionShape(world,onTopPos).isEmpty()){
                    return state;
                }
            }
        }
        return null;
    }

    private static BlockState checkVicinity(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos pos = new BlockPos(x + xx, y, z + zz);
                BlockState state = world.getBlockState(pos);
                if (!state.getCollisionShape(world,pos).isEmpty()) {
                    return state;
                }
            }
        }
        return null;
    }

    private static BlockState checkVicinityBouncy(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockState state = world.getBlockState(new BlockPos(x + xx, y, z + zz));
                if (state.isIn(BlockTags.BEDS) || state.getBlock() == Blocks.SLIME_BLOCK) {
                    return state;
                }
            }
        }
        return null;
    }

    private static BlockState checkVicinityAbove(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos pos = new BlockPos(x + xx, y, z + zz);
                BlockState state = world.getBlockState(pos);
                BlockState oneBelowState = world.getBlockState(new BlockPos(x + xx, y - 1, z + zz));
                if (!state.getCollisionShape(world, pos).isEmpty() && oneBelowState.getBlock() == Blocks.AIR) {
                    return state;
                }
            }
        }
        return null;
    }

    private static BlockState checkVicinityClimbable(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    BlockState state = world.getBlockState(new BlockPos(x + xx, y + yy, z + zz));
                    if (state.getBlock() == Blocks.LADDER ||
                            state.getBlock() == Blocks.TWISTING_VINES ||
                            state.getBlock() == Blocks.TWISTING_VINES_PLANT ||
                            state.getBlock() == Blocks.CAVE_VINES ||
                            state.getBlock() == Blocks.CAVE_VINES_PLANT ||
                            state.getBlock() == Blocks.SCAFFOLDING ||
                            state.getBlock() == Blocks.VINE ||
                            state.getBlock() == Blocks.WEEPING_VINES ||
                            state.getBlock() == Blocks.WEEPING_VINES_PLANT) {
                        return state;
                    }
                }
            }
        }
        return null;
    }

    public static boolean checkVicinitySoul(ServerWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++) {
                    BlockState state = world.getBlockState(new BlockPos(x + xx, y + yy, z + zz));
                    if (state.getBlock() == Blocks.SOUL_SAND ||
                            state.getBlock() == Blocks.SOUL_SOIL) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public static boolean checkPassage(ServerWorld world, MovementPacketData packetView) {
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


    public static BlockState checkBouncyBelow(ServerWorld world, MovementPacketData packetView) {
        int x = (int) Math.round(packetView.getX());
        int y = (int) Math.round(packetView.getY());
        int z = (int) Math.round(packetView.getZ());

        BlockState blockBelow = checkVicinityBouncy(world, x, y - 1, z);

        if (blockBelow != null) {
            return  blockBelow;
        }

        return Blocks.AIR.getDefaultState();
    }


    public static boolean checkClimbable(ServerWorld world, MovementPacketData packetView) {
        int x = (int) Math.round(packetView.getX());
        int y = (int) Math.round(packetView.getY());
        int z = (int) Math.round(packetView.getZ());

        BlockState climbable = checkVicinityClimbable(world, x, y, z);

        return climbable != null;
    }

    public static boolean checkGround(ServerPlayerEntity serverPlayerEntity, MovementPacketData packetView) {

        ServerWorld world = serverPlayerEntity.getServerWorld();
        
        int x = (int) Math.round(packetView.getX());
        int y = (int) Math.round(packetView.getY());
        int z = (int) Math.round(packetView.getZ());

        BlockState blockBelow = checkVicinityGround(world, x, y - 1, z);

        return blockBelow != null;
    }

}