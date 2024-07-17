package me.sebastian420.PandaAC.command;

import com.mojang.brigadier.CommandDispatcher;
import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.storage.PandaConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

import static net.minecraft.server.command.CommandManager.literal;

public class PandaCommand {

    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("panda-ac")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("reloadConfig")
                        .executes( ctx -> reloadConfig(ctx.getSource()))
                )
        );
    }

    public static int reloadConfig(ServerCommandSource source) {
        PandaAC.pandaConfig = PandaConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/PandaAC_config.json"));

        if(source != null)
            source.sendFeedback(
                    () -> Text.literal("Reloaded the config file!").formatted(Formatting.GREEN),
                    false
            );
        return 1;
    }
}
