package me.sebastian420.PandaAC.mixin.illegal_action;// Created 2021-08-06T02:20:50

import me.sebastian420.PandaAC.PandaAC;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents a headless piston from breaking blocks on block updates.
 *
 * @author Ampflower
 **/
@Mixin(PistonBlock.class)
public class PistonMixin_PreventDestruction {
    @Redirect(method = "onSyncedBlockEvent", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean redirectWorld(World world, BlockPos pos, boolean move) {
        if (!PandaAC.pandaConfig.main.preventDestructionByHeadlessPistons || canRemove(world.getBlockState(pos).getBlock())) {
            return world.removeBlock(pos, move);
        } else return false;
    }

    @Unique
    private boolean canRemove(Block block) {
        return block == Blocks.PISTON_HEAD || PandaAC.pandaConfig.main.allowedDestructibleByHeadlessPistons.contains(block);
    }
}
