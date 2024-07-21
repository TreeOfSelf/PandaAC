package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;

public class PlayerMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double currentX;
    private double currentY;
    private double currentZ;

    private double lastAttachedX;
    private double lastAttachedY;
    private double lastAttachedZ;
    private BlockState lastAttachedState;
    private double lastAttachedVelocity;

    public double[] speedPotential = new double[100];
    int speedPotentialPointer = 0;

    public double[] verticalSpeedPotential = new double[100];
    int verticalSpeedPotentialPointer = 0;

    private double carriedPotential = 0;

    private int packetCount;

    private long firstPacketTime;
    private long lastPacketTime;

    private long lastWaterTime;

    private long lastCheck;
    private long airTimeStartTime;
    private long lastSolidTouch;

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

        lastAttachedX = player.getX();
        lastAttachedY = player.getY();
        lastAttachedZ = player.getZ();

        lastAttachedState = Blocks.AIR.getDefaultState();

        airTimeStartTime = 0;
        lastSolidTouch =0;
        carriedPotential = 0;

        changed = true;
        packetCount = 0;
        lastCheck = time;

        firstPacketTime = 0;
        lastPacketTime = time;

        possibleTimer = false;

    }

    public double getX(){return currentX;}
    public double getY(){return currentY;}
    public double getZ(){return currentZ;}
    public double getLastX(){return lastX;}
    public double getLastY(){return lastY;}
    public double getLastZ(){return lastZ;}

    public double getLastAttachedX(){return lastAttachedX;}
    public double getLastAttachedY(){return lastAttachedY;}
    public double getLastAttachedZ(){return lastAttachedZ;}

    public long getAirTimeStartTime(){return airTimeStartTime;}
    public long getLastSolidTouch() {return lastSolidTouch;}

    public boolean getChanged(){return changed;}
    public long getLastCheck(){return lastCheck;}

    public long getLastPacketTime(){return lastPacketTime;}
    public long getFirstPacketTime(){return firstPacketTime;}


    public long getLastWaterTime() {return lastWaterTime;}

    public BlockState getLastAttachedState(){return lastAttachedState;}
    public double getLastAttachedVelocity(){return lastAttachedVelocity;}


    public int getPacketCount(){return packetCount;}
    public boolean getPossibleTimer(){return possibleTimer;}
    public double getCarriedPotential(){return carriedPotential;}


    public void setPossibleTimer(boolean timer){this.possibleTimer = timer;}

    public double getSpeedPotential(double timeModifier){
        return (Arrays.stream(speedPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }

    public double getVerticalSpeedPotential(double timeModifier){
        return (Arrays.stream(verticalSpeedPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }


    public void moveCurrentToLast(long time){
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        changed = false;
        lastCheck = time;
        firstPacketTime = 0;
        Arrays.fill(speedPotential, 0);
        Arrays.fill(verticalSpeedPotential, 0);
        packetCount = 0;
    }

    public void setNew(PlayerMoveC2SPacketView packetView, long time) {
        currentX = packetView.getX();
        currentY = packetView.getY();
        currentZ = packetView.getZ();
        changed = true;
        if (firstPacketTime == 0) firstPacketTime = time;
        lastPacketTime = time;
        packetCount ++;
    }

    public void teleport(double x, double y, double z, long time) {
        currentX = x;
        currentY = y;
        currentZ = z;
        lastX = x;
        lastY = y;
        lastZ = z;
        airTimeStartTime = time;
        Arrays.fill(speedPotential, 0);
        Arrays.fill(verticalSpeedPotential, 0);
    }

    public void setSpeedPotential(double speed) {
        speedPotential[speedPotentialPointer] = speed;
        speedPotentialPointer++;
        if (speedPotentialPointer > speedPotential.length-1) speedPotentialPointer = 0;
    }

    public void setVerticalSpeedPotential(double speed) {
        verticalSpeedPotential[verticalSpeedPotentialPointer] = speed;
        verticalSpeedPotentialPointer++;
        if (verticalSpeedPotentialPointer > verticalSpeedPotential.length-1) verticalSpeedPotentialPointer = 0;
    }

    public void setCarriedPotential(double carriedPotential) {
        this.carriedPotential = carriedPotential;
    }

    public void setLastAttached(double x, double y, double z, BlockState belowState, double velocity, long time) {
        lastAttachedX = x;
        lastAttachedY = y;
        lastAttachedZ = z;
        lastAttachedState = belowState;
        lastAttachedVelocity = Math.abs(velocity);
        airTimeStartTime = time;
        lastSolidTouch = time;
    }


    public void setLastAttachedLiquid(double x, double y, double z, long time) {
        lastAttachedX = x;
        lastAttachedY = y;
        lastAttachedZ = z;
        lastAttachedState = Blocks.AIR.getDefaultState();
        lastAttachedVelocity = 0;
        airTimeStartTime = time;
        lastWaterTime = time;
    }

    public void setAirTimeStartTime(long time) {
        airTimeStartTime = time;
        lastSolidTouch = time;
    }


}
