package me.sebastian420.PandaAC.Objects.Threaded;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;


public class ThreadedWorld {

    private final Map<ChunkCoordinate, ThreadedChunk> chunkMap = new HashMap<>();

    public ThreadedWorld(){

    }

    public void updateChunkData(MinecraftServer minecraftServer, Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        ThreadedChunk threadedChunk = new ThreadedChunk(minecraftServer, chunk);
        chunkMap.put(new ChunkCoordinate(chunkPos.x, chunkPos.z), threadedChunk);
    }

    public void deleteChunkData(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        chunkMap.remove(new ChunkCoordinate(chunkPos.x, chunkPos.z));
    }

    public ThreadedChunk getChunk(BlockPos blockPos){
        int i = blockPos.getX()/16;
        int j = blockPos.getZ()/16;
        return chunkMap.get(new ChunkCoordinate(i, j));
    }

    public BlockState getBlockState(BlockPos pos) {
            ThreadedChunk threadedChunk = this.getChunk(pos);
            return threadedChunk.getBlockState(pos);
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
