package me.sebastian420.PandaAC.check.vehicle;

import me.sebastian420.PandaAC.data.JumpHeights;
import me.sebastian420.PandaAC.manager.CheckManager;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class VehicleJumpHeightCheck {
    public static boolean check(ServerPlayerEntity serverPlayerEntity, VehicleMovementData vehicleData) {
        boolean flagged = false;
        if (vehicleData.getChanged()) {

            Entity vehicle = serverPlayerEntity.getVehicle();
            if (vehicle == null) return false;
            EntityType<?> type = vehicle.getType();
            double checkHeight = 1;

            if (vehicle instanceof AbstractHorseEntity) {
                AbstractHorseEntity horseEntity = (AbstractHorseEntity) vehicle;
                checkHeight = horseEntity.getAttributes().getValue(EntityAttributes.JUMP_STRENGTH) * 6;
            }

            if (vehicleData.getY() - vehicleData.getLastAttachedY() > checkHeight * JumpHeights.FUDGE &&
                    vehicleData.getY() > vehicleData.getLastY()) {
                PandaLogger.getLogger().info("Height dif {} Checkheight {} {}", vehicleData.getY() - vehicleData.getLastAttachedY(), checkHeight, serverPlayerEntity.getPlayerListName());
                CheckManager.rollBackVehicle(serverPlayerEntity, vehicleData);
                flagged = true;
            }
        }
        return flagged;
    }
}
