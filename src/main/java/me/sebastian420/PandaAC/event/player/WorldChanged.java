package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.VehicleMovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;


public class WorldChanged {
    public static void register(){
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(WorldChanged::onPlayerWorldChange);
    }

    private static void onPlayerWorldChange(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld, ServerWorld serverWorld1) {
        PlayerMovementData playerData = MovementManager.getPlayer(serverPlayerEntity);
        playerData.setStarted(false);
        VehicleMovementData vehicleData = VehicleMovementManager.getPlayer(serverPlayerEntity);
        vehicleData.setStarted(false);

    }

}
