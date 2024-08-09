package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.manager.object.FasterWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;

public class FasterWorldManager {

    private static final HashMap<RegistryKey<World>, FasterWorld> worldMap = new HashMap<>();

    public static void createWorld(ServerWorld world){
        worldMap.computeIfAbsent(world.getRegistryKey(), identifier -> new FasterWorld(world));
    }

    public static FasterWorld getWorld(ServerWorld world){
        return worldMap.get(world.getRegistryKey());
    }

}
