package me.sebastian420.PandaAC.trackers.data;

import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class PlayerMoveData implements Data {
    public TeleportConfirmC2SPacket teleportConfirmC2SPacket = null;
    public boolean lastWasTeleportConfirm = false;
    public int expectedTeleportId = 1; //Increments before call (Don't ask)
}
