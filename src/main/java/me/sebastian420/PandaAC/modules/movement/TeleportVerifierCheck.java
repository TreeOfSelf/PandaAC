package me.sebastian420.PandaAC.modules.movement;

import me.sebastian420.PandaAC.events.OutgoingTeleportListener;
import me.sebastian420.PandaAC.events.TeleportConfirmListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;

public class TeleportVerifierCheck extends PAModule implements TeleportConfirmListener, OutgoingTeleportListener {

    public TeleportVerifierCheck() {
        super("teleport_verifier_check");
        TeleportConfirmListener.EVENT.register(this);
        OutgoingTeleportListener.EVENT.register(this);
    }

    private static class TeleportVerifierCheckData {
        public Int2ObjectMap<TeleportInfo> teleports = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    }


    private static class TeleportInfo {
        public final double x;
        public final double y;
        public final double z;
        public final boolean exactX;
        public final boolean exactY;
        public final boolean exactZ;

        public TeleportInfo(PAPlayer player, PlayerPositionLookS2CPacket packet) {
            if (packet.getFlags().contains(PositionFlag.X)) {
                exactX = false;
                x = packet.getX() + player.getPacketX();
            } else {
                exactX = true;
                x = packet.getX();
            }
            if (packet.getFlags().contains(PositionFlag.Y)) {
                exactY = false;
                y = packet.getY() + player.getY();
            } else {
                exactY = true;
                y = packet.getY();
            }
            if (packet.getFlags().contains(PositionFlag.Z)) {
                exactZ = false;
                z = packet.getZ() + player.getZ();
            } else {
                exactZ = true;
                z = packet.getZ();
            }
        }
    }

    @Override
    public void onTeleportConfirm(PAPlayer player, TeleportConfirmC2SPacket teleportPacket, PlayerMoveC2SPacketView movePacket) {
        TeleportInfo teleport = player.getData(TeleportVerifierCheckData.class).teleports.get(teleportPacket.getTeleportId());
        player.getData(TeleportVerifierCheckData.class).teleports.remove(teleportPacket.getTeleportId());
    }

    @Override
    public void onOutgoingTeleport(PAPlayer player, PlayerPositionLookS2CPacket packet) {
        TeleportVerifierCheckData data = player.getOrCreateData(TeleportVerifierCheckData.class, TeleportVerifierCheckData::new);
        data.teleports.put(packet.getTeleportId(), new TeleportInfo(player, packet));
    }
}
