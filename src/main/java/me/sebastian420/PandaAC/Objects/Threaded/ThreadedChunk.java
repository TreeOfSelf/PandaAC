package me.sebastian420.PandaAC.Objects.Threaded;

import io.netty.buffer.Unpooled;
import me.sebastian420.PandaAC.PandaAC;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ThreadedChunk {
    protected ChunkSection[] sectionArray;
    private final int bottomY;
    private final int topY;

    public ThreadedChunk(MinecraftServer minecraftServer, Chunk chunk) {
        this.sectionArray = new ChunkSection[chunk.getHeightLimitView().countVerticalSections()];

        long timeBefore = System.currentTimeMillis();

        fillSectionArray(minecraftServer.getRegistryManager().get(RegistryKeys.BIOME), this.sectionArray);


        ChunkSection[] otherSectionArray = chunk.getSectionArray();
        for (var y = 0; y < otherSectionArray.length; y++){

            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());

            otherSectionArray[y].toPacket(packetByteBuf);
            this.sectionArray[y].readDataPacket(packetByteBuf);
        }

        PandaAC.LOGGER.info("TIME TAKEN {}", System.currentTimeMillis() - timeBefore);

        this.bottomY = chunk.getBottomY();
        this.topY = chunk.getTopY();
    }

    private static void fillSectionArray(Registry<Biome> biomeRegistry, ChunkSection[] sectionArray) {
        for(int i = 0; i < sectionArray.length; ++i) {
            if (sectionArray[i] == null) {
                sectionArray[i] = new ChunkSection(biomeRegistry);
            }
        }

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
