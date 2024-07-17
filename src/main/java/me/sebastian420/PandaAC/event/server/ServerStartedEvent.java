package me.sebastian420.PandaAC.event.server;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStartedEvent {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStartedEvent::serverStarted);
    }

    private static void serverStarted(MinecraftServer minecraftServer) {
        PandaACThread.initialize(minecraftServer);
    }
}
