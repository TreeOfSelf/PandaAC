package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.cast.Player;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class BlockUtil {

    public static BlockState checkVicinityBoat(World world, int x, int y, int z){
        int fastestSpeedLevel = 0;
        BlockState savedState = Blocks.AIR.getDefaultState();

        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++){
                    BlockPos pos = new BlockPos(x + xx, y + yy, z + zz);
                    BlockState state = world.getBlockState(pos);
                    BlockPos onTopPos = pos.offset(Direction.UP, 1);
                    BlockState stateTop = world.getBlockState(onTopPos);

                    if ((state.getFluidState().isIn(FluidTags.WATER) ||
                            state.isIn(BlockTags.ICE)) &&
                            stateTop.getCollisionShape(world, onTopPos).isEmpty()) {

                        if (state.getFluidState().isIn(FluidTags.WATER) && fastestSpeedLevel < 1) {
                            fastestSpeedLevel = 1;
                            savedState = state;
                        } else if (state.isIn(BlockTags.ICE)) {
                            if (state.getBlock() == Blocks.BLUE_ICE && fastestSpeedLevel < 3) {
                                fastestSpeedLevel = 3;
                                savedState = state;
                            } else if (fastestSpeedLevel < 2) {
                                fastestSpeedLevel = 2;
                                savedState = state;
                            }

                        }
                    }
                }
            }
        }
        return savedState;
    }

    public static BlockState checkVicinityIce(World world, int x, int y, int z){
        int fastestSpeedLevel = 0;
        BlockState savedState = Blocks.AIR.getDefaultState();

        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++){
                    BlockPos pos = new BlockPos(x + xx, y + yy, z + zz);
                    BlockState state = world.getBlockState(pos);
                    BlockPos onTopPos = pos.offset(Direction.UP, 1);
                    BlockState stateTop = world.getBlockState(onTopPos);
                    if (state.isIn(BlockTags.ICE) &&
                            stateTop.getCollisionShape(world, onTopPos).isEmpty()) {
                       if (state.isIn(BlockTags.ICE)) {
                            if (state.getBlock() == Blocks.BLUE_ICE && fastestSpeedLevel < 3) {
                                fastestSpeedLevel = 3;
                                savedState = state;
                            } else if (fastestSpeedLevel < 2) {
                                fastestSpeedLevel = 2;
                                savedState = state;
                            }

                        }
                    }
                }
            }
        }
        return savedState;
    }

    public static boolean checkGroundVehicle(Entity vehicle, double y) {
        Entity bottomEntity = vehicle.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = vehicle;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - vehicle.getY() - 0.25005D, 0);

        Iterable<VoxelShape> collidingBlocks = vehicle.getWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        Entity finalBottomEntity = bottomEntity;
        List<Entity> collidingEntities = vehicle.getWorld().getOtherEntities(bottomEntity, bBox, foundEntity -> {
            if (finalBottomEntity.equals(foundEntity)) {
                return false;
            }
            return !(finalBottomEntity).hasPassenger(foundEntity);
        });
        boolean entityCollisions = collidingEntities.iterator().hasNext();

        return blockCollisions || entityCollisions;
    }


    public static BlockState checkFluidVehicle(Entity vehicle, double y) {
        Entity bottomEntity = vehicle.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = vehicle;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - vehicle.getY() - 0.25005D, 0);

        // Get the world from the player
        World world = vehicle.getWorld();

        // Check for fluid blocks within the bounding box
        return BlockPos.stream(bBox)
                .map(world::getBlockState)
                .filter(blockState -> blockState.getFluidState().isStill() || blockState.getBlock() instanceof FluidBlock)
                .findFirst()
                .orElse(Blocks.AIR.getDefaultState());
    }


    public static boolean checkGroundVehicleThicc(Entity vehicle) {
        Entity bottomEntity = vehicle.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = vehicle;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0.25, 0.25, 0.25);

        Iterable<VoxelShape> collidingBlocks = vehicle.getWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        Entity finalBottomEntity = bottomEntity;
        List<Entity> collidingEntities = vehicle.getWorld().getOtherEntities(bottomEntity, bBox, foundEntity -> {
            if (finalBottomEntity.equals(foundEntity)) {
                return false;
            }
            return !(finalBottomEntity).hasPassenger(foundEntity);
        });
        boolean entityCollisions = collidingEntities.iterator().hasNext();

        return blockCollisions || entityCollisions;
    }


    public static boolean checkOtherEntityVehicle(Entity vehicle, double y) {
        Entity bottomEntity = vehicle.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = vehicle;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0.25, 0.25005D, 0.25).offset(0, y - vehicle.getY() - 0.25005D, 0);


        Entity finalBottomEntity = bottomEntity;
        List<Entity> collidingEntities = vehicle.getWorld().getOtherEntities(bottomEntity, bBox, foundEntity -> {
            if (finalBottomEntity.equals(foundEntity)) {
                return false;
            }
            return !(finalBottomEntity).hasPassenger(foundEntity);
        });

        return collidingEntities.iterator().hasNext();
    }




    public static boolean checkGround(ServerPlayerEntity player, double y){

        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - player.getY() - 0.25005D, 0);

        Iterable<VoxelShape> collidingBlocks = player.getWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        if (blockCollisions) {
            ((Player) player).setEntityCollisions(false);
            ((Player) player).setBlockCollisions(true);
        } else {
            Entity finalBottomEntity = bottomEntity;
            List<Entity> collidingEntities = player.getWorld().getOtherEntities(bottomEntity, bBox, entity -> !finalBottomEntity.equals(entity));

            ((Player) player).setEntityCollisions(!collidingEntities.isEmpty());
            ((Player) player).setBlockCollisions(false);
        }

        if(!((Player) player).isNearGround()) {
            return false;
        }

        return true;
    }

    public static BlockState checkFluid(ServerPlayerEntity player, double y) {
        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - player.getY() - 0.25005D, 0);

        // Get the world from the player
        World world = player.getWorld();

        // Check for fluid blocks within the bounding box
        return BlockPos.stream(bBox)
                .map(world::getBlockState)
                .filter(blockState -> blockState.getFluidState().isStill() || blockState.getBlock() instanceof FluidBlock)
                .findFirst()
                .orElse(Blocks.AIR.getDefaultState());
    }

    public static BlockState checkFluidThicc(ServerPlayerEntity player, double y) {
        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0.5, 0.5, 0.5);

        // Get the world from the player
        World world = player.getWorld();

        // Check for fluid blocks within the bounding box
        return BlockPos.stream(bBox)
                .map(world::getBlockState)
                .filter(blockState -> blockState.getFluidState().isStill() || blockState.getBlock() instanceof FluidBlock)
                .findFirst()
                .orElse(Blocks.AIR.getDefaultState());
    }



    public static boolean checkGroundThicc(ServerPlayerEntity player){

        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0.25, 0.25, 0.25);

        Iterable<VoxelShape> collidingBlocks = player.getWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        if (blockCollisions) {
            ((Player) player).setEntityCollisions(false);
            ((Player) player).setBlockCollisions(true);
        } else {
            Entity finalBottomEntity = bottomEntity;
            List<Entity> collidingEntities = player.getWorld().getOtherEntities(bottomEntity, bBox, entity -> !finalBottomEntity.equals(entity));

            ((Player) player).setEntityCollisions(!collidingEntities.isEmpty());
            ((Player) player).setBlockCollisions(false);
        }

        if(!((Player) player).isNearGround()) {
            return false;
        }

        return true;
    }

    public static boolean checkBlocksNearby(ServerPlayerEntity player, double y){

        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(2, 2 ,2);

        Iterable<VoxelShape> collidingBlocks = player.getWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        return blockCollisions || checkFluid(player, y) != Blocks.AIR.getDefaultState();
    }


    public static boolean checkVicinityStairs(ServerWorld world, int x, int y, int z){

        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                for (int yy = -1; yy <= 1; yy++){
                    BlockPos pos = new BlockPos(x + xx, y + yy, z + zz);
                    BlockState state = world.getBlockState(pos);
                    BlockPos onTopPos = pos.offset(Direction.UP, 1);
                    BlockState stateTop = world.getBlockState(onTopPos);

                    if (state.isIn(BlockTags.STAIRS) && stateTop.getCollisionShape(world, onTopPos).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
