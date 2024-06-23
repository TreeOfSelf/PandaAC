package me.sebastian420.PandaAC.modules.movement;

import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.Trackers;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import me.sebastian420.PandaAC.util.CollisionUtil;

public class OnGroundVerticalCheck extends PAModule implements PlayerMovementListener {
    public OnGroundVerticalCheck() {
        super("onground_vertical_check");
        PlayerMovementListener.EVENT.register(this);
    }

    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        if (player.isSpectator()) return;
        float stepHeight = player.getStepHeight();
        if (packet.isChangePosition() &&
            packet.isOnGround() &&
            player.isOnGround() &&
            System.currentTimeMillis() - player.getTracked(Trackers.PLAYER_HIT_GROUND_TRACKER).lastInAir.get() > 500 &&
            !BlockCollisionUtil.isNearby(player, 2.0, 4.0, BlockCollisionUtil.NON_SOLID_COLLISION) &&
            ((stepHeight > 1f) || !CollisionUtil.isNearby(player, packet.getX(), packet.getY(), packet.getZ(), 0.2, 0.5, CollisionUtil.steppablePredicates(stepHeight)))
        ) {
            double ydelta = packet.getY() - player.getPacketY();
            if (ydelta > (stepHeight < 1f ? 0.3 : stepHeight)) flagRollback(player, FlagSeverity.MAJOR, "Player Moved Vertically While onGround " + ydelta);
            if (ydelta < -0.9) flagRollback(player, FlagSeverity.MAJOR, "Player Moved Vertically While onGround " + ydelta);
        }
    }

    public void flagRollback(PAPlayer player, FlagSeverity severity, String message) {
        if (super.flag(player, severity, message)) player.rollback();        
    }
    
}
