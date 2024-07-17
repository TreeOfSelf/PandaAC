package me.sebastian420.PandaAC.Objects.Data;

import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import me.sebastian420.PandaAC.View.PlayerMoveC2SPacketView;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;

public class PlayerMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double currentX;
    private double currentY;
    private double currentZ;

    public double[] speedTimes = new double[10];
    int speedPointer = 0;

    private long lastCheck;
    private long lastShortCheck;
    private boolean changed;


    private final ServerPlayerEntity player;

    public PlayerMovementData(ServerPlayerEntity player){
        long time = System.currentTimeMillis();
        this.player = player;

        lastX = player.getX();
        lastY = player.getY();
        lastZ = player.getZ();

        currentX = player.getX();
        currentY = player.getY();
        currentZ = player.getZ();

        changed = true;
        lastCheck = time;
        lastShortCheck = time;
    }

    public double getX(){return currentX;}
    public double getY(){return currentY;}
    public double getZ(){return currentZ;}
    public double getLastX(){return lastX;}
    public double getLastY(){return lastY;}
    public double getLastZ(){return lastZ;}
    public boolean getChanged(){return changed;}
    public long getLastCheck(){return lastCheck;}
    public long getLastShortCheck(){return lastShortCheck;}

    public double getAverageSpeed(){
        return (Arrays.stream(speedTimes).average().getAsDouble());
    }


    public void moveCurrentToLast(long time){
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        changed = false;
        lastCheck = time;
        save();
    }

    public void save(){
        PlayerMovementDataManager.save(this.player, this);
    }

    public void setNew(PlayerMoveC2SPacketView packetView, long time) {
        currentX = packetView.getX();
        currentY = packetView.getY();
        currentZ = packetView.getZ();
        changed = true;
        lastShortCheck = time;
        save();
    }

    public void rollBack() {
        currentX = lastX;
        currentY = lastY;
        currentZ = lastZ;
    }

    public void setSpeedTime(double speed) {
        speedTimes[speedPointer] = speed;
        speedPointer++;
        if (speedPointer > speedTimes.length-1) speedPointer = 0;

    }
}
