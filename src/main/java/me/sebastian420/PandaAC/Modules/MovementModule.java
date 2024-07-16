package me.sebastian420.PandaAC.Modules;

import me.sebastian420.PandaAC.Objects.Data.PlayerMovementData;
import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import me.sebastian420.PandaAC.PacketViews.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.Util.MathUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class MovementModule {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;
        PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);

        double distance = MathUtil.getDistanceSquared(playerData.getX(), playerData.getZ(), packetView.getX(), packetView.getZ());
        PandaAC.LOGGER.info("Went distance: {}", distance);
        playerData.setNew(packetView);
    }
}
