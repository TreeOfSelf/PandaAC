package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.Modules.MovementModule;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import com.mojang.datafixers.util.Pair;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PandaACThread extends Thread {
    public static final PandaACThread INSTANCE = new PandaACThread();
    public static final BlockingQueue<Pair<ServerPlayerEntity, Packet<ServerPlayPacketListener>>> PACKET_QUEUE = new LinkedBlockingQueue<>();
    public static boolean running = true;
    static {
        INSTANCE.setName("PandaAC");
        INSTANCE.setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Pair<ServerPlayerEntity, Packet<ServerPlayPacketListener>> packetContext = PACKET_QUEUE.take();

                if (packetContext.getSecond() instanceof PlayerMoveC2SPacket packet) {
                    MovementModule.read(packetContext.getFirst(),packet);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}

