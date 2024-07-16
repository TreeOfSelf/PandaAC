package me.sebastian420.PandaAC.Objects;

import me.sebastian420.PandaAC.Objects.Threaded.ThreadedWorld;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public void setBlockState(World world, BlockPos pos, BlockState state) {
        ThreadedWorld threadedWorld = getWorld((ServerWorld) world);
        threadedWorld.setBlockState(pos, state);
    }
}
