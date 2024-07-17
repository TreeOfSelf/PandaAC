package me.sebastian420.PandaAC.Objects;

import me.sebastian420.PandaAC.Objects.Data.PlayerMovementData;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMovementDataManager {
    public static HashMap<UUID, PlayerMovementData> playerMovementMap = new HashMap<>();

    public static PlayerMovementData getPlayer(ServerPlayerEntity player) {
        return playerMovementMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerMovementData(player));
    }

    public static void save(ServerPlayerEntity player, PlayerMovementData data) {
        playerMovementMap.put(player.getUuid(), data);
    }
}
