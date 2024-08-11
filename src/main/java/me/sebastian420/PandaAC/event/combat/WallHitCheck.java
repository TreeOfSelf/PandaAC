package me.sebastian420.PandaAC.event.combat;

import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.sebastian420.PandaAC.PandaAC.pandaConfig;

public class WallHitCheck implements AttackEntityCallback, UseEntityCallback {
    public WallHitCheck() {
    }

    /**
     * Checks if there's a block between a player and entity
     * the player is trying to interact with.
     *
     * @param player player trying to interact with entity.
     * @param victim entity player is trying to interact with.
     * @return {@link ActionResult#FAIL} if player shouldn't be able to hit the victim, otherwise {@link ActionResult#PASS}
     */
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity victim, @Nullable EntityHitResult hitResult) {
        if (pandaConfig.combat.preventWallHit) {
            Vec3d playerPos = player.getCameraPosVec(1.0F);

            Vec3d[] targetPoints = getTargetPoints(victim);

            boolean clearLineOfSight = false;
            for (Vec3d targetPoint : targetPoints) {
                RaycastContext context = new RaycastContext(
                        playerPos,
                        targetPoint,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                );
                BlockHitResult blockHit = world.raycast(context);

                if (blockHit.getType() == HitResult.Type.MISS) {
                    clearLineOfSight = true;
                    break;
                }
            }

            if (!clearLineOfSight) {
                PandaLogger.getLogger().info("FAILED TO HIT MOB BLOCK THROUGH");
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    private static Vec3d @NotNull [] getTargetPoints(Entity victim) {
        Box boundingBox = victim.getBoundingBox();

        Vec3d[] targetPoints = new Vec3d[]{
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ), // Bottom corners
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ), // Top corners
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ),
                boundingBox.getCenter() // Center
        };
        return targetPoints;
    }
}
