package me.sebastian420.PandaAC;

import com.mojang.datafixers.util.Pair;
import me.sebastian420.PandaAC.events.PacketCallback;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PandaACThread extends Thread {
    public static final PandaACThread INSTANCE = new PandaACThread();
    public static final BlockingQueue<Pair<PAPlayer, Packet<ServerPlayPacketListener>>> PACKET_QUEUE = new LinkedBlockingQueue<>();
    public static boolean running = true;
    static {
        INSTANCE.setName("PandaAC");
        INSTANCE.setDaemon(true);

    }

    @Override
    public void run() {
        while (running) {
            try {
                Pair<PAPlayer, Packet<ServerPlayPacketListener>> packetContext = PACKET_QUEUE.take();
                PacketCallback.EVENT.invoker().onPacket(packetContext.getFirst(), packetContext.getSecond());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset interrupted status
                break; // Exit loop if interrupted
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}

