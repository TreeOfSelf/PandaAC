package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.Events.ChunkLoadEvent;
import me.sebastian420.PandaAC.Events.ChunkUnloadEvent;
import me.sebastian420.PandaAC.Events.ServerStopEvent;
import me.sebastian420.PandaAC.Events.WorldLoadEvent;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PandaAC implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("PandaAC");

	@Override
	public void onInitialize() {
		LOGGER.info("PandaAC Started!");
		PandaACThread.INSTANCE.start();
		WorldLoadEvent.register();
		ServerStopEvent.register();
		ChunkLoadEvent.register();
		ChunkUnloadEvent.register();
	}

}
