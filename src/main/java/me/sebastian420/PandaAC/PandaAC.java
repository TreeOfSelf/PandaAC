package me.sebastian420.PandaAC;

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
		ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
		PandaACThread.INSTANCE.start();
	}

	private void serverStopped(MinecraftServer minecraftServer) {
		PandaACThread.running = false;
	}

}
