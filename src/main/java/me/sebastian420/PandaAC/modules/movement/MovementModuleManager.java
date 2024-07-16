package me.sebastian420.PandaAC.modules.movement;

import me.sebastian420.PandaAC.modules.ModuleManager;
import me.sebastian420.PandaAC.modules.movement.elytra.ElytraMaxHorizontalSpeedCheck;
import me.sebastian420.PandaAC.modules.movement.elytra.ElytraMoveCapCheck;
import me.sebastian420.PandaAC.modules.movement.entity.BoatFlyCheck;

public class MovementModuleManager {
    private MovementModuleManager() { }

    public static void init() {
        ModuleManager.registerModule(new SpeedCheck());
        ModuleManager.registerModule(new GlideCheck());
        ModuleManager.registerModule(new BoatFlyCheck());
        ModuleManager.registerModule(new ElytraMoveCapCheck());
        ModuleManager.registerModule(new ElytraMaxHorizontalSpeedCheck());
    }


}


