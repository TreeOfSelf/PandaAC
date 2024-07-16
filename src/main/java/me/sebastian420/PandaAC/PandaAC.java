package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.Events.ServerStopEvent;
import me.sebastian420.PandaAC.Events.WorldLoadEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PandaAC implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("PandaAC");

	@Override
	public void onInitialize() {
		LOGGER.info("PandaAC Started!");
		WorldLoadEvent.register();
		ServerStopEvent.register();
		PandaACThread.INSTANCE.start();
	}

}
