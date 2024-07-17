package me.sebastian420.PandaAC.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PandaConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    /**
     * Main part of the config.
     */
    public static class Main {

        /**
         * Checks whether is doing actions
         * that cannot be done while having the GUI open.
         * (e. g. hitting, typing, etc.)
         */
        public boolean checkInventoryActions = true;


        public boolean preventDestructionByHeadlessPistons = true;

        /**
         * Allows headless pistons to destroy certain blocks when {@link #preventDestructionByHeadlessPistons} is enabled.
         * <p>
         * Useful to allow only breaking of bedrock but denying destruction of barriers, chests and other blocks.
         */
        @JsonAdapter(BlockSetAdapter.class)
        public Set<Block> allowedDestructibleByHeadlessPistons = Collections.singleton(Blocks.PISTON_HEAD);

        @SerializedName("// What altitude in the nether should start inflicting void damage (e.g. 128). -1 disables it.")
        public final String _comment_inflictNetherRoofDamage = "";
        public int inflictNetherRoofDamage = -1;
    }

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
         * Entities that must have health sent to render correctly.
         * <p>
         * K -> Entities to allow health of.
         * V -> Increments by percentage of health to allow.
         * <p>
         * Implied by default is 1F, or alive and dead.
         */
        @JsonAdapter(UnnecessaryEntityTypeMapAdapter.class)
        public Object2FloatOpenHashMap<EntityType<?>> allowedHealthTags = new Object2FloatOpenHashMap<>(
                new EntityType<?>[]{EntityType.WOLF, EntityType.WITHER, EntityType.IRON_GOLEM},
                new float[]{0F, 0.5F, 0.25F}
        );

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

        /**
         * Whether to remove info about ground items.
         * Can prevent chunk banning with items that are lying on ground.
         */
        public boolean removeDroppedItemInfo = true;

        /**
         * Whether to cancel out sending too big packets.
         * Patches "book-banning" and friends.
         */
        public boolean patchItemKickExploit = true;
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

    public final PandaConfig.Main main = new Main();
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

    /**
     * Adapts {@link Block} between it and the identifier.
     *
     * @author Ampflower
     */
    private static final class BlockSetAdapter extends TypeAdapter<Set<Block>> {

        @Override
        public void write(JsonWriter out, Set<Block> value) throws IOException {
            out.beginArray();
            var reg = Registries.BLOCK;
            for (var block : value) {
                out.value(reg.getId(block).toString());
            }
            out.endArray();
        }

        @Override
        public Set<Block> read(JsonReader in) throws IOException {
            in.beginArray();
            var reg = Registries.BLOCK;
            var set = new HashSet<Block>();
            while (in.hasNext()) {
                set.add(reg.get(Identifier.tryParse(in.nextString())));
            }
            in.endArray();
            return set;
        }
    }

    /**
     * Adapts {@link EntityType} between it and the identifier.
     * <p>
     * Unnecessary, as map-level shouldn't be needed to begin with,
     * yet arbitrary unforeseen restrictions require this anyways.
     *
     * @author Ampflower
     */
    private static final class UnnecessaryEntityTypeMapAdapter extends TypeAdapter<Object2FloatOpenHashMap<EntityType<?>>> {

        @Override
        public void write(JsonWriter out, Object2FloatOpenHashMap<EntityType<?>> value) throws IOException {
            out.beginObject();
            var itr = Object2FloatMaps.fastIterator(value);
            while (itr.hasNext()) {
                var entry = itr.next();
                out.name(EntityType.getId(entry.getKey()).toString());
                out.value(entry.getFloatValue());
            }
            out.endObject();
        }

        @Override
        public Object2FloatOpenHashMap<EntityType<?>> read(JsonReader in) throws IOException {
            in.beginObject();
            var map = new Object2FloatOpenHashMap<EntityType<?>>();
            while (in.hasNext()) {
                map.put(EntityType.get(in.nextName()).orElseThrow(() -> new IOException("Invalid entity type.")),
                        (float) in.nextDouble());
            }
            in.endObject();
            return map;
        }
    }
}
