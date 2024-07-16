package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.Modules.MovementModule;
import me.sebastian420.PandaAC.Objects.ThreadedWorldManager;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PandaACThread extends Thread {
    public static final PandaACThread INSTANCE = new PandaACThread();
    private static final BlockingQueue<QueuedEvent> EVENT_QUEUE = new LinkedBlockingQueue<>();
    public static final ThreadedWorldManager threadedWorldManager = new ThreadedWorldManager();

    public static boolean running = true;

    static {
        INSTANCE.setName("PandaAC");
        INSTANCE.setDaemon(true);
    }

    private enum EventType {
        WORLD_LOAD,
        CHUNK_LOAD,
        CHUNK_UNLOAD,
        PLAYER_MOVE,
        BLOCK_CHANGE,
    }

    private static class QueuedEvent {
        EventType type;
        Object data;

        QueuedEvent(EventType type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    public static void queueWorldLoad(ServerWorld world) {
        EVENT_QUEUE.offer(new QueuedEvent(EventType.WORLD_LOAD, world));
    }

    public static void queueChunkLoad(ServerWorld world, Chunk chunk) {
        EVENT_QUEUE.offer(new QueuedEvent(EventType.CHUNK_LOAD, new Object[]{world, chunk}));
    }

    public static void queueChunkUnload(ServerWorld world, Chunk chunk) {
        EVENT_QUEUE.offer(new QueuedEvent(EventType.CHUNK_UNLOAD, new Object[]{world, chunk}));
    }

    public static void queuePlayerMove(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        EVENT_QUEUE.offer(new QueuedEvent(EventType.PLAYER_MOVE, new Object[]{player, packet}));
    }

    public static void queueBlockChange(World world, BlockPos blockPos, BlockState state) {
        EVENT_QUEUE.offer(new QueuedEvent(EventType.BLOCK_CHANGE, new Object[]{world, blockPos, state}));
    }

    @Override
    public void run() {
        while (running) {
            try {
                QueuedEvent event = EVENT_QUEUE.take();
                processEvent(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processEvent(QueuedEvent event) {
        switch (event.type) {
            case WORLD_LOAD:
                ServerWorld world = (ServerWorld) event.data;
                threadedWorldManager.createWorld(world);
                break;
            case CHUNK_LOAD:
                Object[] chunkLoadData = (Object[]) event.data;
                threadedWorldManager.getWorld((ServerWorld) chunkLoadData[0]).updateChunkData(((ServerWorld) chunkLoadData[0]).getServer(),(Chunk) chunkLoadData[1]);
                break;
            case CHUNK_UNLOAD:
                Object[] chunkUnloadData = (Object[]) event.data;
                threadedWorldManager.getWorld((ServerWorld) chunkUnloadData[0]).deleteChunkData((Chunk) chunkUnloadData[1]);
                break;
            case BLOCK_CHANGE:
                Object[] blockChangeData = (Object[]) event.data;
                threadedWorldManager.setBlockState((World) blockChangeData[0], (BlockPos) blockChangeData[1], (BlockState) blockChangeData[2]);
                break;
            case PLAYER_MOVE:
                Object[] moveData = (Object[]) event.data;
                ServerPlayerEntity player = (ServerPlayerEntity) moveData[0];
                if (!player.isDisconnected()) {
                    MovementModule.read(player, (PlayerMoveC2SPacket) moveData[1]);
                }
                break;
        }
    }
}