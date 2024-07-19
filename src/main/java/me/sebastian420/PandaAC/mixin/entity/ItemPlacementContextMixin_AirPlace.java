package me.sebastian420.PandaAC.mixin.entity;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemPlacementContext.class, priority = 5000)
public class ItemPlacementContextMixin_AirPlace {
    @Inject(method = "canPlace", at = @At(value = "HEAD"), cancellable = true)
    public void prePlace(CallbackInfoReturnable<Boolean> cir)
    {
        ItemPlacementContext itemPlacementContext = (ItemPlacementContext)(Object)this;
        FasterWorld world = PandaACThread.fasterWorldManager.getWorld((ServerWorld) itemPlacementContext.getWorld());

        boolean canPlace = false;
        for (Direction direction : Direction.values()) {
            BlockPos checkPos = itemPlacementContext.getBlockPos().offset(direction, 1);
            BlockState blockState = world.getBlockState(checkPos);
            if (!blockState.getCollisionShape(world.realWorld, checkPos).isEmpty()) {
                canPlace = true;
                break;
            }
        }
        if (!canPlace) cir.setReturnValue(false);
    }
}