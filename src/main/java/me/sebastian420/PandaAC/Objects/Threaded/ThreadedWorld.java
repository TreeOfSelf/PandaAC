package me.sebastian420.PandaAC.Objects.Threaded;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.HashMap;
import java.util.Map;


public class ThreadedWorld {

    private final Map<ChunkCoordinate, ThreadedChunk> chunkMap = new HashMap<>();

    public ThreadedWorld(){

    }

    public void updateChunkData(World world, int i, int j) {
        Chunk chunk = world.getChunk(i, j, ChunkStatus.FULL);
        ThreadedChunk threadedChunk = new ThreadedChunk(chunk.getSectionArray());
        chunkMap.put(new ChunkCoordinate(i, j), threadedChunk);
    }

    private record ChunkCoordinate(int i, int j) {

        @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                ChunkCoordinate that = (ChunkCoordinate) o;

                if (i != that.i) return false;
                return j == that.j;
            }

    }
}
