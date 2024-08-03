package me.sebastian420.PandaAC;

import me.sebastian420.PandaAC.event.player.BreakBlock;
import me.sebastian420.PandaAC.event.player.UseEntity;
import me.sebastian420.PandaAC.event.player.UseRocket;
import me.sebastian420.PandaAC.event.server.ServerStartedEvent;
import me.sebastian420.PandaAC.event.server.ServerStopEvent;
import me.sebastian420.PandaAC.event.world.ChunkLoadEvent;
import me.sebastian420.PandaAC.event.world.ChunkUnloadEvent;
import me.sebastian420.PandaAC.event.S2CPacket.S2CPacketModule;
import me.sebastian420.PandaAC.event.combat.CombatModule;
import me.sebastian420.PandaAC.event.world.WorldLoadEvent;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import me.sebastian420.PandaAC.command.PandaCommand;
import me.sebastian420.PandaAC.storage.PandaConfig;

import java.io.File;

public class PandaAC implements ModInitializer {

	public static PandaConfig pandaConfig;

	@Override
	public void onInitialize() {
		pandaConfig = PandaConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/PandaAC_config.json"));

		CommandRegistrationCallback.EVENT.register((dispatcher, ignored, ignored1) ->
				PandaCommand.registerCommand(dispatcher));


		PandaLogger.getLogger().info("PandaAC Started!");

		// Events
		ServerStartedEvent.register();
		WorldLoadEvent.register();
		ServerStopEvent.register();
		ChunkLoadEvent.register();
		ChunkUnloadEvent.register();
		UseEntity.register();
		UseRocket.register();
		BreakBlock.register();
		CombatModule.registerEvents();
		S2CPacketModule.registerEvents();
	}
}
