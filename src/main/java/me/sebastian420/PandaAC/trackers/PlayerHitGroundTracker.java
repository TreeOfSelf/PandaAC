package me.sebastian420.PandaAC.trackers;

import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.data.PlayerHitGroundData;
import org.jetbrains.annotations.NotNull;

public class PlayerHitGroundTracker extends Tracker<PlayerHitGroundData> implements PlayerMovementListener {
    PlayerHitGroundTracker() {
        super(PlayerHitGroundData.class);
        PlayerMovementListener.EVENT.register(this);
    }

    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        if (!packet.isOnGround()) {
            get(player).lastInAir.set(System.currentTimeMillis());
        }
    }

    @Override
    @NotNull
    public PlayerHitGroundData get(PAEntity entity) {
        return entity.getOrCreateData(PlayerHitGroundData.class, PlayerHitGroundData::new);
    }
}
