package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;


public class WindCharged {
    public static void register(){
        ServerEntityEvents.ENTITY_UNLOAD.register(WindCharged::onEntityUnloaded);
    }

    private static void onEntityUnloaded(Entity entity, ServerWorld serverWorld) {
        if (entity.getType() == EntityType.WIND_CHARGE) {
            Box boundingBox = new Box(entity.getPos().add(-5, -5, -5), entity.getPos().add(5, 5, 5));
            List<PlayerEntity> nearbyPlayers = serverWorld.getEntitiesByClass(PlayerEntity.class, boundingBox, (player) -> true);
            for (PlayerEntity player : nearbyPlayers) {
                PlayerMovementData playerData = MovementManager.getPlayer((ServerPlayerEntity) player);
                playerData.setStoredSpeed(playerData.getStoredSpeed() + 30.0);
                playerData.setStoredSpeedVertical(playerData.getStoredSpeedVertical() + 75.0);
            }
        }
    }
}
