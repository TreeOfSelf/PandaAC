package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class VehicleMovementManager {

    public static HashMap<UUID, VehicleMovementData> vehicleMovementMap = new HashMap<>();

    public static VehicleMovementData getPlayer(ServerPlayerEntity player) {
        return vehicleMovementMap.computeIfAbsent(player.getUuid(), uuid -> new VehicleMovementData(player));
    }

    public static void read(ServerPlayerEntity player, VehicleMoveC2SPacket packet, long time) {
        VehicleMovementData vehicleData = getPlayer(player);

        Entity vehicle = player.getVehicle();

        if (vehicle == null) return;

        EntityType<?> type = vehicle.getType();

        double speedPotential = 0;

        if (type == EntityType.BOAT) {
            boolean blockUnder = BlockUtil.checkGroundVehicle(vehicle, packet.getY());
            if (blockUnder) {
                speedPotential = SpeedLimits.BOAT_LAND;
            } else {
                speedPotential = SpeedLimits.BOAT_AIR;
            }
        }

        vehicleData.setSpeedPotential(speedPotential);
        vehicleData.setNew(packet, time);
        //FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());
    }
}
