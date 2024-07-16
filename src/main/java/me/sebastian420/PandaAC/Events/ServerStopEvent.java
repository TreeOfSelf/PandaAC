package me.sebastian420.PandaAC.Events;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class ServerStopEvent {
    public static void register() {
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerStopEvent::serverStopped);
    }

    private static void serverStopped(MinecraftServer minecraftServer) {
        PandaACThread.running = false;
    }
}
