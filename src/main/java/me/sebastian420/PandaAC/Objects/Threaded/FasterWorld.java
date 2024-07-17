package me.sebastian420.PandaAC.Objects.Threaded;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

/*
I don't know why, but world.getBlockState or anything that calls world.getChunk is INCREDIBLY slow
It is literally hundreds of times faster, to just cache chunks based on X/Z in a hashmap and pull the chunks from that
than to use ANYTHING that calls world.getChunk
*/

public class FasterWorld {

    private final Map<ChunkCoordinate, Chunk> chunkMap = new HashMap<>();

    public FasterWorld(){

    }

    public void updateChunkData(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        chunkMap.put(new ChunkCoordinate(chunkPos.x, chunkPos.z), chunk);
    }

    public void deleteChunkData(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        chunkMap.remove(new ChunkCoordinate(chunkPos.x, chunkPos.z));
    }

    public Chunk getChunk(BlockPos blockPos){
        int i = blockPos.getX()/16;
        int j = blockPos.getZ()/16;
        return chunkMap.get(new ChunkCoordinate(i, j));
    }

    public BlockState getBlockState(BlockPos pos) {
        Chunk threadedChunk = this.getChunk(pos);
        if (threadedChunk == null) return Blocks.AIR.getDefaultState();
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
