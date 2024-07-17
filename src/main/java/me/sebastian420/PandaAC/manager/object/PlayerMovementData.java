package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.manager.PlayerMovementDataManager;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;

public class PlayerMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double currentX;
    private double currentY;
    private double currentZ;

    public double[] speedPotential = new double[100];
    int speedPotentialPointer = 0;

    private int packetCount;
    private long lastShortCheck;
    private long lastCheck;
    private boolean changed;

    private boolean possibleTimer;


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
        packetCount = 0;
        lastCheck = time;
        lastShortCheck = time;

        possibleTimer = false;
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
    public int getPacketCount(){return packetCount;}
    public boolean getPossibleTImer(){return possibleTimer;}


    public void setPossibleTimer(boolean timer){this.possibleTimer = timer;}



    public double getSpeedPotential(){
        return (Arrays.stream(speedPotential).sum());
    }


    public void moveCurrentToLast(long time){
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        changed = false;
        lastCheck = time;
        Arrays.fill(speedPotential, 0);
        packetCount =0;
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
        packetCount ++;
        save();
    }

    public void rollBack() {
        currentX = lastX;
        currentY = lastY;
        currentZ = lastZ;
        packetCount = 0;
        save();
    }

    public void setSpeedPotential(double speed) {
        speedPotential[speedPotentialPointer] = speed;
        speedPotentialPointer++;
        if (speedPotentialPointer > speedPotential.length-1) speedPotentialPointer = 0;

    }
}
