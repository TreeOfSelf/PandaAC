package me.sebastian420.PandaAC.modules;


import me.sebastian420.PandaAC.modules.exploit.ExploitModuleManager;
import me.sebastian420.PandaAC.modules.movement.MovementModuleManager;
import me.sebastian420.PandaAC.modules.packetcount.PacketCountModuleManager;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private ModuleManager() { }

    private static final ArrayList<PAModule> MODULES = new ArrayList<>();

    public static void init() {
        PacketCountModuleManager.init();
        ExploitModuleManager.init();
        MovementModuleManager.init();
    }

    public static void registerModule(PAModule check) {
        MODULES.add(check);
    }

    public static List<PAModule> getModules() {
        return MODULES;
    }

    public static int getModuleCount() {
        return MODULES.size();
    }

}
