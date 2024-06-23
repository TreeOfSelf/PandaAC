package me.sebastian420.PandaAC.objects.entity;

import me.sebastian420.PandaAC.events.ClickSlotC2SPacketCallback;
import me.sebastian420.PandaAC.modules.PAModule;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.util.ActionResult;

/* Package Private */ class PAPlayerEx {
    static {
        ClickSlotC2SPacketCallback.EVENT.register((player, packet) -> {
            if (packet.getSyncId() == 0 && player.asMcPlayer().currentScreenHandler == player.asMcPlayer().playerScreenHandler) {
                player.setHasCurrentPlayerScreenHandler(true);
            }
            return ActionResult.PASS;
        });
    }

    public PAPlayerEx(PAPlayer player) {
        this.lastGoodX = player.getX();
        this.lastGoodY = player.getY();
        this.lastGoodZ = player.getZ();

        this.lastPacketX = player.getX();
        this.lastPacketY = player.getY();
        this.lastPacketZ = player.getZ();

        this.lastPacketYaw = player.getYaw();
        this.lastPacketPitch = player.getPitch();
    }

    public final Object2LongMap<PAModule> lastFlagsMap = Object2LongMaps.synchronize(new Object2LongOpenHashMap<>());

    public volatile double flags = 0.0;
    public volatile long lastFlag = 0;

    public volatile double lastGoodX;
    public volatile double lastGoodY;
    public volatile double lastGoodZ;
    public volatile boolean hasLastGood = false;

    public volatile double lastPacketX;
    public volatile double lastPacketY;
    public volatile double lastPacketZ;
    public volatile float lastPacketYaw;
    public volatile float lastPacketPitch;

    public volatile boolean hasCurrentPlayerScreenHandler = false;
}
