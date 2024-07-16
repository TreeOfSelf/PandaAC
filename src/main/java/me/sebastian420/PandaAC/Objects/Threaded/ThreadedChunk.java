package me.sebastian420.PandaAC.Objects.Threaded;

import net.minecraft.world.chunk.ChunkSection;

public class ThreadedChunk {
    protected final ChunkSection[] sectionArray;

    public ThreadedChunk(ChunkSection[] sectionArray) {
        this.sectionArray = sectionArray;
    }
}
