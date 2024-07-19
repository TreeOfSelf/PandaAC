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
        Entity vehicle = player.getVehicle();
        return vehicleMovementMap.computeIfAbsent(player.getUuid(), uuid -> new VehicleMovementData(player, vehicle));
    }

    public static void read(ServerPlayerEntity player, VehicleMoveC2SPacket packet, long time) {

        Entity vehicle = player.getVehicle();


        VehicleMovementData vehicleData = getPlayer(player);


        if (vehicle == null) return;

        EntityType<?> type = vehicle.getType();

        double speedPotential = 0;
        double yawPotential = 0;

        if (type == EntityType.BOAT) {
            yawPotential = SpeedLimits.BOAT_YAW_WATER;
            boolean blockUnder = BlockUtil.checkGroundVehicle(vehicle, packet.getY());
            if (blockUnder) {
                speedPotential = SpeedLimits.BOAT_LAND;
            } else {
                speedPotential = SpeedLimits.BOAT_AIR;
            }
        }

        vehicleData.setSpeedPotential(speedPotential);
        vehicleData.setYawPotential(yawPotential);
        vehicleData.setNew(packet, vehicle.getUuid());
        //FasterWorld fasterWorld = PandaACThread.fasterWorldManager.getWorld(player.getServerWorld());
    }
}
