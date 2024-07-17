package me.sebastian420.PandaAC.event.world;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkLoadEvent {
    public static void register() {
        ServerChunkEvents.CHUNK_LOAD.register(ChunkLoadEvent::chunkLoad);
    }

    private static void chunkLoad(ServerWorld serverWorld, WorldChunk worldChunk) {
        PandaACThread.queueChunkLoad(serverWorld, worldChunk);
    }
}
