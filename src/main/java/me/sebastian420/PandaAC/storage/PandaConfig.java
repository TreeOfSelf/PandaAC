package me.sebastian420.PandaAC.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.sebastian420.PandaAC.util.PandaLogger;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class PandaConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();


    /**
     * Outgoing packet settings.
     */
    public static class Packet {

        /**
         * Whether to remove the teleport data
         * from packets when entities move out of
         * view distance.
         * Status: working
         */
        public boolean removeTeleportData = true;

        /**
         * Removes entity health data from packets
         * sent to client.
         * <p>
         * Status: working.
         */
        public boolean removeHealthTags = true;

        /**
         * Removes entity equipment tags from
         * packets. Players will still see if item is enchanted,
         * but won't get the durability or stack size information.
         * <p>
         * Status: working.
         */
        public boolean removeEquipmentTags = true;

        /**
         * Whether to remove original coordinates for the
         * sound, e.g. when summoning a wither / when lightning bolt strikes.
         */
        public boolean patchSoundExploits = true;

    }

    /**
     * Movement checks settings.
     */
    public static class Movement {
        /**
         * Client can tell server its onGround status and
         * server blindly accepts it. This can allow
         * client to not take any fall damage.
         * This setting re-enables the check server-side
         * and doesn't care about the client's onGround status.
         */
        public boolean patchNoFall = true;
    }

    /**
     * Combat checks settings.
     */
    public static class Combat {
        /**
         * Checks if player is hitting entity through wall.
         */
        public boolean preventWallHit = true;

        /**
         * Checks if player is using reach hacks.
         */
        public boolean checkHitDistance = true;

        /**
         * Checks the angle at which player is hitting the entity.
         */
        public boolean checkHitAngle = true;
    }

    public final PandaConfig.Combat combat = new Combat();

    public static class Duplication {
        public boolean patchSaveLimit = true;
        public boolean patchGravityBlock = true;
        public boolean patchDeathDuplication = true;
    }
    public final PandaConfig.Packet packet = new Packet();
    public final PandaConfig.Movement movement = new Movement();
    public final PandaConfig.Duplication duplication = new Duplication();

    /**
     * Loads PandaAC config from file.
     *
     * @param configFile file to read PandaAC config from.
     * @return PandaConfig object
     */
    public static PandaConfig loadConfig(File configFile) {
        PandaConfig pandaConfig;
        if(configFile.exists() && configFile.isFile()) {
            try(
                    FileInputStream fileInputStream = new FileInputStream(configFile);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                pandaConfig = GSON.fromJson(bufferedReader, PandaConfig.class);
            } catch (IOException e) {
                PandaLogger.logError("[PandaAC] Problem occurred when trying to load config: " + e.getMessage());
                pandaConfig = new PandaConfig();
            }
        } else {
            pandaConfig = new PandaConfig();
        }
        pandaConfig.saveConfig(configFile);

        return pandaConfig;
    }

    /**
     * Saves PandaAC config to the file.
     *
     * @param configFile file where to save config to.
     */
    public void saveConfig(File configFile) {
        try (
                FileOutputStream stream = new FileOutputStream(configFile);
                Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)
        ) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            PandaLogger.logError("Problem occurred when saving config: " + e.getMessage());
        }
    }


}
