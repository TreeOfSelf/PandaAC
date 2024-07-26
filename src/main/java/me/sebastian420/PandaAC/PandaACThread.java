package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.manager.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PandaACThread extends Thread {
    public static PandaACThread INSTANCE;
    private static final BlockingQueue<QueuedEvent> EVENT_QUEUE = new LinkedBlockingQueue<>();
    public static final FasterWorldManager fasterWorldManager = new FasterWorldManager();

    private final MinecraftServer minecraftServer;

    private int tickCount = 0;

    public static boolean running = true;


    public PandaACThread(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }


    private enum EventType {
        WORLD_LOAD,
        CHUNK_LOAD,
        CHUNK_UNLOAD,
        PLAYER_MOVE,
        PLAYER_TELEPORT,
        PLAYER_VELOCITY,
        VEHICLE_MOVE,
        SERVER_VEHICLE_MOVE,
        TICK,
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
        EVENT_QUEUE.add(new QueuedEvent(EventType.WORLD_LOAD, world));
    }

    public static void queueChunkLoad(ServerWorld world, Chunk chunk) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.CHUNK_LOAD, new Object[]{world, chunk}));
    }

    public static void queueChunkUnload(ServerWorld world, Chunk chunk) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.CHUNK_UNLOAD, new Object[]{world, chunk}));
    }

    public static void queuePlayerMove(ServerPlayerEntity player, PlayerMoveC2SPacket packet, long packetTime) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.PLAYER_MOVE, new Object[]{player, packet, packetTime}));
    }

    public static void queueVehicleMove(ServerPlayerEntity player, VehicleMoveC2SPacket packet, long packetTime) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.VEHICLE_MOVE, new Object[]{player, packet, packetTime}));
    }

    public static void queuePlayerTeleport(ServerPlayerEntity player, PlayerPositionLookS2CPacket packet) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.PLAYER_TELEPORT, new Object[]{player, packet}));
    }

    public static void queuePlayerVelocity(ServerPlayerEntity player, EntityVelocityUpdateS2CPacket packet) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.PLAYER_VELOCITY, new Object[]{player, packet}));

    }

    public static void queueServerVehicleMove(ServerPlayerEntity player, VehicleMoveS2CPacket packet) {
        EVENT_QUEUE.add(new QueuedEvent(EventType.SERVER_VEHICLE_MOVE, new Object[]{player, packet}));
    }

    public static void initialize(MinecraftServer minecraftServer) {

        INSTANCE = new PandaACThread(minecraftServer);
        INSTANCE.setName("PandaAC");
        INSTANCE.setDaemon(true);
        INSTANCE.start();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            EVENT_QUEUE.add(new QueuedEvent(EventType.TICK, null));
        });
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

        ServerPlayerEntity player;

        switch (event.type) {
            case WORLD_LOAD:
                ServerWorld world = (ServerWorld) event.data;
                fasterWorldManager.createWorld(world);
                break;
            case CHUNK_LOAD:
                Object[] chunkLoadData = (Object[]) event.data;
                fasterWorldManager.getWorld((ServerWorld) chunkLoadData[0]).updateChunkData(((ServerWorld) chunkLoadData[0]).getServer(),(Chunk) chunkLoadData[1]);
                break;
            case CHUNK_UNLOAD:
                Object[] chunkUnloadData = (Object[]) event.data;
                fasterWorldManager.getWorld((ServerWorld) chunkUnloadData[0]).deleteChunkData((Chunk) chunkUnloadData[1]);
                break;
            case PLAYER_MOVE:
                Object[] moveData = (Object[]) event.data;
                player = (ServerPlayerEntity) moveData[0];
                if (!player.isDisconnected()) {
                    MovementManager.read(player, (PlayerMoveC2SPacket) moveData[1], (long) moveData[2]);
                }
                break;
            case PLAYER_TELEPORT:
                Object[] teleportData = (Object[]) event.data;
                player = (ServerPlayerEntity) teleportData[0];
                if (!player.isDisconnected()) {
                    MovementManager.receiveTeleport(player, (PlayerPositionLookS2CPacket) teleportData[1]);
                }
                break;
            case PLAYER_VELOCITY:
                Object[] velocityData = (Object[]) event.data;
                player = (ServerPlayerEntity) velocityData[0];
                if (!player.isDisconnected()) {
                    MovementManager.receiveVelocity(player, (EntityVelocityUpdateS2CPacket) velocityData[1]);
                }
                break;
            case VEHICLE_MOVE:
                Object[] vehicleMoveData = (Object[]) event.data;
                player = (ServerPlayerEntity) vehicleMoveData[0];
                if (!player.isDisconnected()) {
                    VehicleMovementManager.read(player, (VehicleMoveC2SPacket) vehicleMoveData[1], (long) vehicleMoveData[2]);
                }
                break;
            case SERVER_VEHICLE_MOVE:
                Object[] serverVehicleMoveData = (Object[]) event.data;
                player = (ServerPlayerEntity) serverVehicleMoveData[0];
                if (!player.isDisconnected()) {
                    VehicleMovementManager.setData(player, (VehicleMoveS2CPacket) serverVehicleMoveData[1]);
                }
                break;
            case TICK:
                tickCount++;
                //Handle every 5 ticks
                if (tickCount % 5 == 0) {
                    long time = System.currentTimeMillis();
                    for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
                        if (!serverPlayerEntity.isDisconnected()) {
                            CheckManager.run(serverPlayerEntity, time);
                        }
                    }
                }
                break;
        }
    }
}