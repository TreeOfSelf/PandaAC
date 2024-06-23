package me.sebastian420.PandaAC.modules;

import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.text.Text;

public class PAModule {
    public final String moduleName;

    public PAModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getFlagCoolDownMs() {
        return 1000;
    }

    public boolean flag(PAPlayer player, FlagSeverity severity, String message) {

        if(player.asMcPlayer().isAlive()) {

            player.asMcPlayer().sendMessage(Text.literal("Flagged: " + message), true);

            if (severity == FlagSeverity.MAJOR) {
                player.asMcPlayer().getServer().getPlayerManager().getPlayerList().forEach(player1 -> {
                    PAPlayer player2 = PAPlayer.of(player1);
                    player2.asMcPlayer().sendMessage(Text.literal(player.asMcPlayer().getGameProfile().getName() + " was major flagged: " + message), false);
                });
            } else {
                player.asMcPlayer().getServer().getPlayerManager().getPlayerList().forEach(player1 -> {
                    PAPlayer player2 = PAPlayer.of(player1);
                    player2.asMcPlayer().sendMessage(Text.literal(player.asMcPlayer().getGameProfile().getName() + " was minor flagged: " + message), false);
                });
            }
        }
        return player.flag(this, severity);
    }

    public boolean assertOrFlag(boolean condition, PAPlayer player, FlagSeverity severity, String message) {
        if (!condition) return flag(player, severity, message);
        return false;
    }

    public enum FlagSeverity {
        MINOR, //Fixable/Likely False Positive
        MAJOR //Admins Should Investigate
    }
}
