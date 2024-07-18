package me.sebastian420.PandaAC.manager.object;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;


public class FasterWorld {

    private final Map<ChunkPos, FasterChunk> chunkMap = new HashMap<>();
    public final ServerWorld realWorld;

    public FasterWorld(ServerWorld realWorld){
        this.realWorld = realWorld;
    }

    public void updateChunkData(MinecraftServer server, Chunk chunk) {
        chunkMap.put(chunk.getPos(), new FasterChunk(server,chunk));
    }

    public void deleteChunkData(Chunk chunk) {
        chunkMap.remove(chunk.getPos());
    }

    public FasterChunk getChunk(BlockPos blockPos){
        int i = ChunkSectionPos.getSectionCoord(blockPos.getX());
        int j = ChunkSectionPos.getSectionCoord(blockPos.getZ());
        return chunkMap.get(new ChunkPos(i, j));
    }


    public BlockState getBlockState(BlockPos pos) {
        FasterChunk chunk = this.getChunk(pos);
        if (chunk == null) return Blocks.AIR.getDefaultState();
        return chunk.getBlockState(pos);
    }

}