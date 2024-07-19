package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.cast.Player;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

public class BlockUtil {

    public static BlockState checkVicinityBoat(FasterWorld world, int x, int y, int z){
        for(int xx = -1; xx <= 1; xx ++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos pos = new BlockPos(x + xx, y, z + zz);
                BlockState state = world.getBlockState(pos);
                BlockPos onTopPos = pos.offset(Direction.UP,1);
                BlockState stateTop = world.getBlockState(onTopPos);

                if ((state.getBlock() == Blocks.WATER ||
                        state.getBlock() == Blocks.ICE ||
                        state.getBlock() == Blocks.BLUE_ICE ||
                        state.getBlock() == Blocks.FROSTED_ICE ||
                        state.getBlock() == Blocks.PACKED_ICE
                ) &&
                        stateTop.getCollisionShape(world.realWorld,onTopPos).isEmpty()){
                    return state;
                }
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    public static boolean checkGroundVehicle(Entity vehicle, double y) {
        Entity bottomEntity = vehicle.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = vehicle;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - vehicle.getY() - 0.25005D, 0);

        Iterable<VoxelShape> collidingBlocks = vehicle.getEntityWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        Entity finalBottomEntity = bottomEntity;
        List<Entity> collidingEntities = vehicle.getEntityWorld().getOtherEntities(bottomEntity, bBox, foundEntity -> {
            if (finalBottomEntity.equals(foundEntity)) {
                return false;
            }
            return !(finalBottomEntity).hasPassenger(foundEntity);
        });
        boolean entityCollisions = collidingEntities.iterator().hasNext();

        return blockCollisions || entityCollisions;
    }



    public static boolean checkGround(ServerPlayerEntity player, double y){

        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - player.getY() - 0.25005D, 0);

        Iterable<VoxelShape> collidingBlocks = player.getEntityWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        if (blockCollisions) {
            ((Player) player).setEntityCollisions(false);
            ((Player) player).setBlockCollisions(true);
        } else {
            Entity finalBottomEntity = bottomEntity;
            List<Entity> collidingEntities = player.getEntityWorld().getOtherEntities(bottomEntity, bBox, entity -> !finalBottomEntity.equals(entity));

            ((Player) player).setEntityCollisions(!collidingEntities.isEmpty());
            ((Player) player).setBlockCollisions(false);
        }

        if(!((Player) player).isNearGround()) {
            return false;
        }

        return true;
    }

}
