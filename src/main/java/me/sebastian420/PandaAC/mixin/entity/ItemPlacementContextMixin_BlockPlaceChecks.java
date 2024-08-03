package me.sebastian420.PandaAC.mixin.entity;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemPlacementContext.class, priority = 5000)
public class ItemPlacementContextMixin_BlockPlaceChecks {
    @Inject(method = "canPlace", at = @At(value = "HEAD"), cancellable = true)
    public void prePlace(CallbackInfoReturnable<Boolean> cir)
    {
        ItemPlacementContext itemPlacementContext = (ItemPlacementContext)(Object)this;
        FasterWorld world = PandaACThread.fasterWorldManager.getWorld((ServerWorld) itemPlacementContext.getWorld());
        PlayerEntity player = itemPlacementContext.getPlayer();
        Vec3d hitPos = itemPlacementContext.getHitPos();
        double distance = player.squaredDistanceTo(hitPos);
        boolean canPlace = false;

        //Check air place
        for (Direction direction : Direction.values()) {
            BlockPos checkPos = itemPlacementContext.getBlockPos().offset(direction, 1);
            BlockState blockState = world.getBlockState(checkPos);
            if (!blockState.getCollisionShape(world.realWorld, checkPos).isEmpty()) {
                canPlace = true;
                break;
            }
        }

        //Check reach
        if (distance > 18) {
            canPlace = false;
        }


        //Check place through block

        // Through-wall hit check
        BlockHitResult blockHit = (BlockHitResult) player.raycast(Math.sqrt(64.0), 0, false);
        if (Math.sqrt(blockHit.squaredDistanceTo(player)) + 0.5D < distance) {
            canPlace = false;
        }

        //Check angle
        int xOffset = player.getHorizontalFacing().getOffsetX();
        int zOffset = player.getHorizontalFacing().getOffsetZ();

        if (xOffset * hitPos.x - xOffset * player.getX() < 0 || zOffset * hitPos.z - zOffset * player.getZ() < 0) {
            canPlace = false;
        }

        double deltaX = hitPos.x - player.getX();
        double deltaZ = hitPos.z - player.getZ();
        double beta = Math.atan2(deltaZ, deltaX) - Math.PI / 2;
        double phi = beta - Math.toRadians(player.getYaw());
        double allowedAttackSpace = 1.0;

        if (Math.abs(distance * Math.sin(phi)) > allowedAttackSpace / 2 + 0.2D) {
            canPlace = false;
        }

        if (!canPlace) cir.setReturnValue(false);
    }
}