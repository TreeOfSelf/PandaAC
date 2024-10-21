package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.CheckManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerLeave {
    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register(PlayerLeave::onLeave);
    }

    private static void onLeave(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer minecraftServer) {
        long time = System.currentTimeMillis();
        CheckManager.run(serverPlayNetworkHandler.player,time);
    }
}
