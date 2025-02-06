package me.sebastian420.PandaAC.manager.object;

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
    }


    public MovementPacketData(VehicleMoveC2SPacket packet) {
        this.x = packet.position().getX();
        this.y = packet.position().getY();
        this.z = packet.position().getZ();
        this.yaw = packet.yaw();
        this.pitch = packet.pitch();
    }

    public MovementPacketData(VehicleMoveS2CPacket packet) {
        this.x = packet.position().getX();
        this.y = packet.position().getY();
        this.z = packet.position().getZ();
        this.yaw = packet.yaw();
        this.pitch = packet.pitch();
    }

    public MovementPacketData(PlayerPositionLookS2CPacket packet) {

        this.x = packet.change().position().x;
        this.y = packet.change().position().y;
        this.z = packet.change().position().z;
        this.yaw = packet.change().yaw();
        this.pitch = packet.change().pitch();
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
}
