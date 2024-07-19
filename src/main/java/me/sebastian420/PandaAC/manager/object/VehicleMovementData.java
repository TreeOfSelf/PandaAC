package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.data.SpeedLimits;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;


public class VehicleMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double currentX;
    private double currentY;
    private double currentZ;

    public double[] speedPotential = new double[100];
    int speedPotentialPointer = 0;

    private double carriedPotential;

    private boolean changed;
    private boolean possibleTimer;

    private int packetCount = 0;

    private long lastCheck;

    private final ServerPlayerEntity player;

    public VehicleMovementData(ServerPlayerEntity player){
        this.player = player;

        lastX = player.getX();
        lastY = player.getY();
        lastZ = player.getZ();

        currentX = player.getX();
        currentY = player.getY();
        currentZ = player.getZ();

        lastCheck = System.currentTimeMillis();

    }

    public void setNew(VehicleMoveC2SPacket packet, long time) {
        currentX = packet.getX();
        currentY = packet.getY();
        currentZ = packet.getZ();
        changed = true;
        packetCount++;
    }

    public void moveCurrentToLast(long time) {
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        changed = false;
        packetCount = 0;
        lastCheck = time;
        Arrays.fill(speedPotential, 0);
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
    public double getLastX(){return lastX;}
    public double getLastY(){return lastY;}
    public double getLastZ(){return lastZ;}
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

}

