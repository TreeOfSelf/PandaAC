package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.VehicleMovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerJoin {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(PlayerJoin::onJoin);
    }

    private static void onJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        VehicleMovementData vehicleData = VehicleMovementManager.getPlayer(player);
        PlayerMovementData playerData = MovementManager.getPlayer(player);
        playerData.setInitial(player);
        vehicleData.setInitial(player);
    }
}
