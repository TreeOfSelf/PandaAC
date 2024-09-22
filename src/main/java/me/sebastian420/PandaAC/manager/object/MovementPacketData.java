package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;

public class MovementPacketData {

    double x;
    double y;
    double z;
    double yaw;
    double pitch;


    public MovementPacketData(PlayerMoveC2SPacketView packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public MovementPacketData(VehicleMoveC2SPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public MovementPacketData(VehicleMoveS2CPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public MovementPacketData(PlayerPositionLookS2CPacket packet) {
        this.x = packet.getX();
        this.y = packet.getY();
        this.z = packet.getZ();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
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
