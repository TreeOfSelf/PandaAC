package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.VehicleMovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class PlayerChangeDimension {
    public static void register() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(PlayerChangeDimension::onPlayerChangeWorld);
    }

    private static void onPlayerChangeWorld(ServerPlayerEntity player, ServerWorld serverWorld, ServerWorld serverWorld1) {
        VehicleMovementData vehicleData = VehicleMovementManager.getPlayer(player);
        PlayerMovementData playerData = MovementManager.getPlayer(player);
        playerData.setInitial(player);
        vehicleData.setInitial(player);
    }

}
