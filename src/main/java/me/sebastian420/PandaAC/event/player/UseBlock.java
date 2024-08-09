package me.sebastian420.PandaAC.event.player;


import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import javax.swing.*;

public class UseBlock {
    public static void register(){
        UseBlockCallback.EVENT.register(UseBlock::onUseBlock);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {

        if (player.isCreative()) return ActionResult.PASS;

        Vec3d hitPos = blockHitResult.getPos();
        double distance = player.getCameraPosVec(1.0f).squaredDistanceTo(hitPos);

        //Check reach
        if (distance > 22) {
            PandaLogger.getLogger().info("Distance place check failed {}", distance);
            return ActionResult.FAIL;
        }


        //Check place through block
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = hitPos;
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
        BlockHitResult result = world.raycast(context);

        if (result.getType() == HitResult.Type.BLOCK && !result.getBlockPos().equals(blockHitResult.getBlockPos())) {
            PandaLogger.getLogger().info("Through-block BlocKEntity check failed: hit block at {}", result.getBlockPos());
            return ActionResult.FAIL;
        }


        //Check angle
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d placeVec = hitPos.subtract(player.getPos()).normalize();

        double angle = Math.acos(lookVec.dotProduct(placeVec));

        double maxAllowedAngle = 48;

        if (angle > maxAllowedAngle) {
            PandaLogger.getLogger().info("Angle check BlockEntity failed: angle {}", Math.toDegrees(angle));
            return ActionResult.FAIL;
        }


        return ActionResult.PASS;
    }


}
