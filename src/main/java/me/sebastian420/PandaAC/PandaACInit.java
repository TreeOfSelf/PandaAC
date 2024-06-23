package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.events.EventManager;
import me.sebastian420.PandaAC.modules.ModuleManager;
import me.sebastian420.PandaAC.trackers.Trackers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PandaACInit implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("PandaAC");
	private static final String EQUALS_LINE = "========================================";

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
		LOGGER.info(EQUALS_LINE);
		LOGGER.info("PandaAC Loading");
		LOGGER.info(EQUALS_LINE);
		LOGGER.info("Initializing Events");
		EventManager.init();
		LOGGER.info("Finished Initializing Events");
		LOGGER.info("Initializing Trackers...");
		long trackerInitStart = System.currentTimeMillis();
		Trackers.init();
		LOGGER.info("Loaded {} Trackers in {}ms", Trackers.getTrackerCount(), System.currentTimeMillis() - trackerInitStart);
		LOGGER.info("Initializing Checks...");
		long checkInitStart = System.currentTimeMillis();
		ModuleManager.init();
		LOGGER.info("Loaded {} Modules in {}ms", ModuleManager.getModuleCount(), System.currentTimeMillis() - checkInitStart);
		LOGGER.info("Initializing Compatability Manager");
		LoggerThread.INSTANCE.start();
		PandaACThread.INSTANCE.start();
		LOGGER.info(EQUALS_LINE);
		LOGGER.info("PandaAC Loaded");
		LOGGER.info(EQUALS_LINE);
	}

	private void serverStopped(MinecraftServer minecraftServer) {
		LoggerThread.running = false;
		PandaACThread.running = false;

	}

}
