package me.sebastian420.PandaAC.Events;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStartEvent {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerStartEvent::serverStarted);
    }

    private static void serverStarted(MinecraftServer minecraftServer) {
        PandaACThread.initialize(minecraftServer);
    }
}
