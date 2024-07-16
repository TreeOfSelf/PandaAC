package me.sebastian420.PandaAC.Objects.Threaded;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ThreadedChunk {
    protected ChunkSection[] sectionArray;
    private final int bottomY;
    private final int topY;

    public ThreadedChunk(Chunk chunk) {
        this.sectionArray = chunk.getSectionArray().clone();
        this.bottomY = chunk.getBottomY();
        this.topY = chunk.getTopY();
    }

    int getBottomSectionCoord() {
        return ChunkSectionPos.getSectionCoord(this.bottomY);
    }

    int sectionCoordToIndex(int coord) {
        return coord - this.getBottomSectionCoord();
    }

    int getSectionIndex(int y) {
        return this.sectionCoordToIndex(ChunkSectionPos.getSectionCoord(y));
    }

    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        int l = this.getSectionIndex(j);
        if (l >= 0 && l < this.sectionArray.length) {
            ChunkSection chunkSection = this.sectionArray[l];
            if (!chunkSection.isEmpty()) {
                return chunkSection.getBlockState(i & 15, j & 15, k & 15);
            }
        }

        return Blocks.AIR.getDefaultState();
    }
}
