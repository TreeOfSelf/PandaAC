package me.sebastian420.PandaAC.modules.movement.entity;

import me.sebastian420.PandaAC.events.VehicleMoveListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAEntity;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import me.sebastian420.PandaAC.util.CollisionUtil;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class EntityVerticalCheck extends PAModule implements VehicleMoveListener {
    public EntityVerticalCheck() {
        super("entity_vertical_check");
        VehicleMoveListener.EVENT.register(this);
    }

    @Override
    public long getFlagCoolDownMs() {
        return 100;
    }

    private class EntityVerticalCheckData {
        volatile double maxY = 0.0;
        boolean isActive = false;

        long startAfter = System.currentTimeMillis();
    }

    @Override
    public void onVehicleMove(PAPlayer player, PAEntity vehicle, PlayerMoveC2SPacketView playerLook, PlayerInputC2SPacket playerInput, VehicleMoveC2SPacket vehicleMoveC2SPacket, @Nullable VehicleMoveC2SPacket lastVehicleMoveC2SPacket) {
            if (vehicle == null) return;
            EntityVerticalCheckData verticalCheckData = player.getOrCreateData(EntityVerticalCheckData.class,
                    EntityVerticalCheckData::new);
            if (vehicle.asMcEntity().isSwimming()
                    || BlockCollisionUtil.isNearby(player, 2.0, 4.0, BlockCollisionUtil.NON_SOLID_COLLISION)) {
                verticalCheckData.isActive = false;
                return;
            }
            Box vehicleBox = vehicle.getBoxForPosition(vehicleMoveC2SPacket.getX(), vehicleMoveC2SPacket.getY(), vehicleMoveC2SPacket.getZ()).expand(0.01);
            Box scanBox = vehicleBox.expand(0.6);
            boolean vehicleOnGround = CollisionUtil.isTouching(new PAEntity[] { player, vehicle }, scanBox,
                    vehicle.getWorld(), CollisionUtil.touchingRigidTopPredicates(vehicleBox));
            if (player.getWorld().getTime() - vehicle.getPistonMovementTick() < 1000) {
                verticalCheckData.isActive = false;
            }
            else if (vehicle.isOnGround() && !vehicleOnGround && vehicle.getVelocity().getY() < 0.45) {
                verticalCheckData.maxY = vehicle.getY() + vehicle.getMaxJumpHeight();
                verticalCheckData.isActive = true;
            } else if (vehicleOnGround) {
                if (verticalCheckData != null && verticalCheckData.isActive) {
                    verticalCheckData.isActive = false;
                }
            } else { // Packet off ground
                if (verticalCheckData.isActive && vehicleMoveC2SPacket.getY() > verticalCheckData.maxY) {
                    if (flag(player, FlagSeverity.MAJOR,
                            "Failed Entity Vertical Movement Check " + (verticalCheckData.maxY - vehicleMoveC2SPacket.getY())))
                        player.groundBoat(vehicle);
                }
                if (!verticalCheckData.isActive && vehicle.getVelocity().getY() < 0.45) {
                    verticalCheckData.maxY = vehicle.getY() + vehicle.getMaxJumpHeight();
                    verticalCheckData.isActive = true;
                }
    
            }
    }
}
