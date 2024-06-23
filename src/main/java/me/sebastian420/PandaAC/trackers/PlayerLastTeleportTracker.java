package me.sebastian420.PandaAC.trackers;

import me.sebastian420.PandaAC.events.OutgoingTeleportListener;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.data.PlayerLastTeleportData;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.jetbrains.annotations.NotNull;

public class PlayerLastTeleportTracker extends Tracker<PlayerLastTeleportData> implements OutgoingTeleportListener {

    protected PlayerLastTeleportTracker() {
        super(PlayerLastTeleportData.class);
        OutgoingTeleportListener.EVENT.register(this);
    }

    @Override
    @NotNull
    public PlayerLastTeleportData get(PAEntity entity) {
        return entity.getOrCreateData(PlayerLastTeleportData.class, PlayerLastTeleportData::new);
    }

    @Override
    public void onOutgoingTeleport(PAPlayer player, PlayerPositionLookS2CPacket packet) {
        PlayerLastTeleportData data = get(player);
        data.lastTeleportX = packet.getX();
        data.lastTeleportY = packet.getY();
        data.lastTeleportZ = packet.getZ();
        data.lastTeleportYaw = packet.getYaw();
        data.lastTeleportPitch = packet.getPitch();
        data.lastTeleport = System.currentTimeMillis();
    }
    
}
