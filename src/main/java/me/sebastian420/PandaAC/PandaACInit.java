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

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
		LOGGER.info("PandaAC Started!");
		EventManager.init();
		ModuleManager.init();
		PandaACThread.INSTANCE.start();
	}

	private void serverStopped(MinecraftServer minecraftServer) {
		PandaACThread.running = false;
	}

}
