package me.sebastian420.PandaAC.modules.packetcount;

import me.sebastian420.PandaAC.modules.ModuleManager;

public class PacketCountModuleManager {
    private PacketCountModuleManager() { }

    public static void init() {
        ModuleManager.registerModule(new PacketLimiterCheck());
        ModuleManager.registerModule(new CraftRequestC2SPacketLimiterModule());
    }
}
