package me.sebastian420.PandaAC.mixin.entity;

import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
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

        World world = itemPlacementContext.getWorld();
        PlayerEntity player = itemPlacementContext.getPlayer();
        Vec3d hitPos = itemPlacementContext.getHitPos();

        if (!player.isCreative()) {


            double distance = player.getCameraPosVec(1.0f).squaredDistanceTo(hitPos);
            boolean canPlace = false;

            //Check air place
            for (Direction direction : Direction.values()) {
                BlockPos checkPos = itemPlacementContext.getBlockPos().offset(direction, 1);
                BlockState blockState = world.getBlockState(checkPos);

                if (!blockState.isAir() && blockState.getBlock() != Blocks.WATER && blockState.getBlock() != Blocks.LAVA) {
                    canPlace = true;
                    break;
                }
            }

            if (!canPlace) PandaLogger.getLogger().info("Air place check failed");


            //Check reach
            if (distance > 22) {
                PandaLogger.getLogger().info("Distance place check failed {}", distance);
                canPlace = false;
            }


            //Check place through block
            Vec3d start = player.getCameraPosVec(1.0F);
            Vec3d end = hitPos;
            RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
            BlockHitResult result = world.raycast(context);

            if (result.getType() == HitResult.Type.BLOCK && !result.getBlockPos().equals(itemPlacementContext.getBlockPos())) {
                canPlace = false;
                PandaLogger.getLogger().info("Through-block place check failed: hit block at {}", result.getBlockPos());
            }


            //Check angle
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d placeVec = hitPos.subtract(player.getPos()).normalize();

            double angle = Math.acos(lookVec.dotProduct(placeVec));

            double maxAllowedAngle = 48;

            if (angle > maxAllowedAngle) {
                canPlace = false;
                PandaLogger.getLogger().info("Angle check failed: angle {}", Math.toDegrees(angle));
            }

            if (!canPlace) cir.setReturnValue(false);
        }

    }
}