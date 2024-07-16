package me.sebastian420.PandaAC.Objects;

import me.sebastian420.PandaAC.Objects.Threaded.ThreadedWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ThreadedWorldManager {

    public ThreadedWorldManager() {

    }

    private final HashMap<Identifier, ThreadedWorld> worldMap = new HashMap<>();

    public void createWorld(ServerWorld world){
        worldMap.computeIfAbsent(world.getRegistryKey().getRegistry(), identifier -> new ThreadedWorld());
    }

    public ThreadedWorld getWorld(ServerWorld world){
        return worldMap.get(world.getRegistryKey().getRegistry());
    }

}
