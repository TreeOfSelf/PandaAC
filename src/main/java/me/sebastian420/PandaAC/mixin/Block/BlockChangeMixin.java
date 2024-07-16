package me.sebastian420.PandaAC.mixin.Block;

import me.sebastian420.PandaAC.Events.BlockChangeEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class BlockChangeMixin {

    @Inject(method = "onStateReplaced", at = @At("TAIL"))
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (!state.isOf(newState.getBlock())) {
            BlockChangeEvent.change(world, pos, newState);
        }

    }
}