package me.sebastian420.PandaAC.event.world;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class WorldLoadEvent {
    public static void register() {
        ServerWorldEvents.LOAD.register(WorldLoadEvent::onWorldLoad);
    }

    private static void onWorldLoad(MinecraftServer minecraftServer, ServerWorld serverWorld) {
        PandaACThread.queueWorldLoad(serverWorld);
    }
}
