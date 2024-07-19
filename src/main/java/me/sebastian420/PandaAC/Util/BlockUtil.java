package me.sebastian420.PandaAC.util;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.cast.Player;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

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


    public static boolean betterCheckGround(ServerPlayerEntity player, double y){

        Entity bottomEntity = player.getRootVehicle();
        if (bottomEntity == null) {
            bottomEntity = player;
        }
        final Box bBox = bottomEntity.getBoundingBox().expand(0, 0.25005D, 0).offset(0, y - player.getY() - 0.25005D, 0);

        Iterable<VoxelShape> collidingBlocks = player.getEntityWorld().getBlockCollisions(bottomEntity, bBox);
        boolean blockCollisions = collidingBlocks.iterator().hasNext();

        if (blockCollisions) {
            // Preferring block collisions over entity ones
            ((Player) player).setEntityCollisions(false);
            ((Player) player).setBlockCollisions(true);
        } else {
            Entity finalBottomEntity = bottomEntity;
            List<Entity> collidingEntities = player.getEntityWorld().getOtherEntities(bottomEntity, bBox, entity -> !finalBottomEntity.equals(entity));

            ((Player) player).setEntityCollisions(!collidingEntities.isEmpty());
            ((Player) player).setBlockCollisions(false);
        }

        if(!((Player) player).isNearGround()) {
            // Player isn't on ground but client packet says it is
            return false;
        }

        return true;
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
