package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.FasterWorld;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.PacketUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.UUID;

public class VehicleMovementManager {

    public static HashMap<UUID, VehicleMovementData> vehicleMovementMap = new HashMap<>();

    public static VehicleMovementData getPlayer(ServerPlayerEntity player) {
        return vehicleMovementMap.computeIfAbsent(player.getUuid(), uuid -> new VehicleMovementData(player));
    }

    public static void read(ServerPlayerEntity player, VehicleMoveC2SPacket packet, long time) {

        VehicleMovementData vehicleData = getPlayer(player);
        vehicleData.setNew(packet, time);
        //FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());
    }
}
