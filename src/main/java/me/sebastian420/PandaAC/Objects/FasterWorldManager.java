package me.sebastian420.PandaAC.Objects;

import me.sebastian420.PandaAC.Objects.Threaded.FasterWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class FasterWorldManager {

    public FasterWorldManager() {

    }

    private final HashMap<Identifier, FasterWorld> worldMap = new HashMap<>();

    public void createWorld(ServerWorld world){
        worldMap.computeIfAbsent(world.getRegistryKey().getRegistry(), identifier -> new FasterWorld());
    }

    public FasterWorld getWorld(ServerWorld world){
        return worldMap.get(world.getRegistryKey().getRegistry());
    }

}
