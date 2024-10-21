package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class MovementPacketData {

    double x;
    double y;
    double z;
    double yaw;
    double pitch;

    public MovementPacketData(PlayerMoveC2SPacket packet, ServerPlayerEntity serverPlayerEntity) {
        this.x = packet.getX(serverPlayerEntity.getX());
        this.y = packet.getY(serverPlayerEntity.getY());
        this.z = packet.getZ(serverPlayerEntity.getZ());
        this.yaw = packet.getYaw(serverPlayerEntity.getYaw());
        this.pitch = packet.getPitch(serverPlayerEntity.getPitch());

        /*if (Math.abs(this.x) < 10 || Math.abs(this.z) < 10) {
            PandaLogger.getLogger().info("PlayerMoveC2SPacket");
            PandaLogger.getLogger().info("{} {} {} {}", packet.changesPosition(), packet.changesLook(), packet.getX(serverPlayerEntity.getX()), packet.getZ(serverPlayerEntity.getZ()));
        }*/
    }


    public MovementPacketData(VehicleMoveC2SPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
        /*if (Math.abs(this.x) < 10 || Math.abs(this.z) < 10) {
            PandaLogger.getLogger().info("VehicleMoveC2SPacket");
        }*/
    }

    public MovementPacketData(VehicleMoveS2CPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
        /*if (Math.abs(this.x) < 10 || Math.abs(this.z) < 10) {
            PandaLogger.getLogger().info("VehicleMoveS2CPacket");
        }*/
    }

    public MovementPacketData(PlayerPositionLookS2CPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
        /*if (Math.abs(this.x) < 10 || Math.abs(this.z) < 10) {
            PandaLogger.getLogger().info("PlayerPositionLookS2CPacket");
        }*/
    }

    public MovementPacketData(EntityVelocityUpdateS2CPacket packet) {
        this.x = packet.getVelocityX();
        this.y = packet.getVelocityY();
        this.z = packet.getVelocityZ();
    }



    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public double getZ(){
        return this.z;
    }
    public double getYaw(){
        return this.yaw;
    }
    public double getPitch(){
        return this.pitch;
    }
}
