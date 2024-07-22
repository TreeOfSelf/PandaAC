package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public class UseEntity {
    public static void register(){
        UseEntityCallback.EVENT.register(UseEntity::onUseEntity);
    }

    private static ActionResult onUseEntity(PlayerEntity player, net.minecraft.world.World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (entity instanceof VehicleEntity) {
            PlayerMovementData playerData = MovementManager.getPlayer((ServerPlayerEntity) player);
            if (!BlockUtil.checkBlocksNearby((ServerPlayerEntity) player, playerData.getY())) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}
