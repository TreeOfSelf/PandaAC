package me.sebastian420.PandaAC.mixin.entity;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
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
        double distance = player.getCameraPosVec(1.0f).squaredDistanceTo(hitPos);
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
        if (distance > 22) {
            PandaLogger.getLogger().info("Distance place check failed {}", distance);
            canPlace = false;
        }


        //Check place through block
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = hitPos;
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
        BlockHitResult result = world.realWorld.raycast(context);

        if (result.getType() == HitResult.Type.BLOCK && !result.getBlockPos().equals(itemPlacementContext.getBlockPos())) {
            canPlace = false;
            PandaLogger.getLogger().info("Through-block place check failed: hit block at {}", result.getBlockPos());
        }


        //Check angle
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d placeVec = hitPos.subtract(player.getPos()).normalize();

        double angle = Math.acos(lookVec.dotProduct(placeVec));

        double maxAllowedAngle = Math.PI / 4;

        if (angle > maxAllowedAngle) {
            canPlace = false;
            PandaLogger.getLogger().info("Angle check failed: angle {}", Math.toDegrees(angle));
        }


        if (!canPlace) cir.setReturnValue(false);
    }
}