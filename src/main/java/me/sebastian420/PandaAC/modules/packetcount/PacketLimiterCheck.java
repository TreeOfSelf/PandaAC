package me.sebastian420.PandaAC.modules.packetcount;

import me.sebastian420.PandaAC.events.PacketCallback;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class PacketLimiterCheck extends PAModule implements PacketCallback {
    private static final long INTERVAL = 7;
    private static final long MAX_PACKETS_PER_SECOND = 500;

    public PacketLimiterCheck() {
        super("packet_limiter");
        PacketCallback.EVENT.register(this);
    }

    @Override
    public ActionResult onPacket(PAPlayer player, Packet<ServerPlayPacketListener> packet) {
        PacketVolumeData data = player.getOrCreateData(PacketVolumeData.class, PacketVolumeData::new);
        data.packetCount++;
        if (data.packetCount > MAX_PACKETS_PER_SECOND * INTERVAL) {
            player.getNetworkHandler().disconnect(Text.literal("Too Many Packets"));
            return ActionResult.FAIL;
        } else if (System.currentTimeMillis() - data.lastCheck >= INTERVAL * 1000) {
            data.packetCount = 0;
            data.lastCheck = System.currentTimeMillis();
        }
        return ActionResult.PASS;
    }

    public static class PacketVolumeData {
        public long packetCount = 0;
        public long lastCheck = System.currentTimeMillis();
    }
}
