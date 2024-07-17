package me.sebastian420.PandaAC.Events;

import me.sebastian420.PandaAC.PandaACThread;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkUnloadEvent {

    public static void register() {
        ServerChunkEvents.CHUNK_UNLOAD.register(ChunkUnloadEvent::chunkUnload);
    }

    private static void chunkUnload(ServerWorld serverWorld, WorldChunk worldChunk) {
        PandaACThread.queueChunkUnload(serverWorld, worldChunk);
    }

}
