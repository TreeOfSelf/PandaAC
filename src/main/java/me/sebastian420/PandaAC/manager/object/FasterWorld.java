package me.sebastian420.PandaAC.manager.object;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class FasterWorld {

    private final Map<ChunkPos, Chunk> chunkMap = new HashMap<>();
    public final ServerWorld realWorld;

    public FasterWorld(ServerWorld realWorld){
        this.realWorld = realWorld;
    }

    public void updateChunkData(Chunk chunk) {
        chunkMap.put(chunk.getPos(), chunk);
    }

    public void deleteChunkData(Chunk chunk) {
        chunkMap.remove(chunk.getPos());
    }

    public Chunk getChunk(BlockPos blockPos){
        int i = blockPos.getX()/16;
        int j = blockPos.getZ()/16;
        return chunkMap.get(new ChunkPos(i, j));
    }

    public BlockState getBlockState(BlockPos pos) {
        Chunk chunk = this.getChunk(pos);
        if (chunk == null) return Blocks.AIR.getDefaultState();
        return chunk.getBlockState(pos);
    }

}