package me.sebastian420.PandaAC.event.combat;

import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static me.sebastian420.PandaAC.PandaAC.pandaConfig;

public class AngleCheck implements UseEntityCallback, AttackEntityCallback {

    private static final double MAX_ATTACK_ANGLE = Math.toRadians(70);

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity victim, @Nullable EntityHitResult hitResult) {
        if (pandaConfig.combat.checkHitAngle) {
            if (!isValidAttackAngle(player, victim)) {
                PandaLogger.getLogger().info("FAILED TO HIT ENTITY: INVALID ANGLE");
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }


    private boolean isValidAttackAngle(PlayerEntity player, Entity victim) {
        Vec3d playerPos = player.getEyePos();
        Vec3d playerLook = player.getRotationVector();
        Vec3d victimCenter = victim.getBoundingBox().getCenter();

        Vec3d toVictim = victimCenter.subtract(playerPos).normalize();

        double dotProduct = playerLook.dotProduct(toVictim);
        double angle = Math.acos(dotProduct);

        return angle <= MAX_ATTACK_ANGLE;
    }
}
