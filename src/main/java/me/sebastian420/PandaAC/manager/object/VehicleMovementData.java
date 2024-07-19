package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.data.SpeedLimits;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.UUID;


public class VehicleMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;
    private double lastYaw;

    private double currentX;
    private double currentY;
    private double currentZ;
    private double currentYaw;

    public double[] speedPotential = new double[100];
    int speedPotentialPointer = 0;

    public double[] yawPotential = new double[100];
    int yawPotentialPointer = 0;

    private double carriedPotential;

    private boolean changed;
    private boolean possibleTimer;

    private int packetCount = 0;

    private long lastCheck;

    private UUID vehicleUUID;

    private final ServerPlayerEntity player;

    public VehicleMovementData(ServerPlayerEntity player, Entity vehicle){
        this.player = player;

        if (vehicle != null) {
            lastX = vehicle.getX();
            lastY = vehicle.getY();
            lastZ = vehicle.getZ();

            currentX = vehicle.getX();
            currentY = vehicle.getY();
            currentZ = vehicle.getZ();

            currentYaw = vehicle.getYaw();
            lastYaw = vehicle.getYaw();
        }

        lastCheck = System.currentTimeMillis();

    }

    public void setNew(VehicleMoveC2SPacket packet, UUID vehicleUUID) {
        currentX = packet.getX();
        currentY = packet.getY();
        currentZ = packet.getZ();
        currentYaw = packet.getYaw();
        changed = true;
        packetCount++;

        if (!vehicleUUID.equals(this.vehicleUUID)) {
            this.vehicleUUID = vehicleUUID;
            lastX = currentX;
            lastY = currentY;
            lastZ = currentZ;
            lastYaw = currentYaw;
        }



    }

    public void moveCurrentToLast(long time) {
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        lastYaw = currentYaw;
        changed = false;
        packetCount = 0;
        lastCheck = time;
        Arrays.fill(speedPotential, 0);
        Arrays.fill(yawPotential, 0);
    }

    public void teleport(double x, double y, double z, long time) {
        currentX = x;
        currentY = y;
        currentZ = z;
        lastX = x;
        lastY = y;
        lastZ = z;
    }


    public boolean getChanged() {return changed;}
    public boolean getPossibleTimer() {return possibleTimer;}

    public long getLastCheck() {return lastCheck;}

    public double getX(){return currentX;}
    public double getY(){return currentY;}
    public double getZ(){return currentZ;}
    public double getYaw(){return currentYaw;}
    public double getLastX(){return lastX;}
    public double getLastY(){return lastY;}
    public double getLastZ(){return lastZ;}
    public double getLastYaw(){return lastYaw;}

    public double getCarriedPotential(){return carriedPotential;}

    public int getPacketCount(){return packetCount;}

    public void setPossibleTimer(boolean timer){this.possibleTimer = timer;}
    public void setCarriedPotential(double carriedPotential) {this.carriedPotential = carriedPotential;}

    public void setSpeedPotential(double speed) {
        speedPotential[speedPotentialPointer] = speed;
        speedPotentialPointer++;
        if (speedPotentialPointer > speedPotential.length-1) speedPotentialPointer = 0;
    }

    public double getSpeedPotential(double timeModifier){
        return (Arrays.stream(speedPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }

    public void setYawPotential(double yaw) {
        yawPotential[yawPotentialPointer] = yaw;
        yawPotentialPointer++;
        if (yawPotentialPointer > yawPotential.length-1) yawPotentialPointer = 0;
    }

    public double getYawPotential(double timeModifier){
        return (Arrays.stream(yawPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }

}
