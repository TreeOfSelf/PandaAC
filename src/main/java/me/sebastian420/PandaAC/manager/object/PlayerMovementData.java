package me.sebastian420.PandaAC.manager.object;

import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;

public class PlayerMovementData {

    private Vec3d lastVelocity;

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

    private double storedSpeed;
    private double storedSpeedVertical;
    private double lastSpeed;

    private double lastSpeedPotential;

    private double elytraElevation;
    private double elytraMaxElevation;
    private long elytraLastRocketTime;

    public double[] speedPotential = new double[5];
    int speedPotentialPointer = 0;

    public double[] verticalSpeedPotential = new double[5];
    int verticalSpeedPotentialPointer = 0;

    public double[] averageSpeed = new double[5];
    int averageSpeedPointer = 0;

    private double carriedPotential = 0;

    private int packetCount;
    private int speedFlagCount;
    private int shortSpeedFlagCount;
    private int upSpeedFlagCount;
    private int elytraHoverCount = 0;

    private int playerMovePackets = 0;


    private long firstPacketTime;
    private long lastPacketTime;
    private long lastFluidTime;
    private long lastElytraStoreTime;
    private long lastLevitation;

    private long lastCheck;
    private long airTimeStartTime;
    private long lastSolidTouch;

    private boolean changed;
    private boolean hover;
    private boolean onGround;
    private boolean flying;
    private boolean onIce;
    private boolean hasStarted = false;

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
        lastSolidTouch = 0;
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
    public void setAirTimeStartTime(long time){ airTimeStartTime = time;}

    public long getLastSolidTouch() {return lastSolidTouch;}
    public void setLastSolidTouch(long time) {lastSolidTouch = time;}

    public boolean getChanged(){return changed;}
    public long getLastCheck(){return lastCheck;}

    public long getLastPacketTime(){return lastPacketTime;}
    public long getFirstPacketTime(){return firstPacketTime;}

    public long getLastElytraStoreTime() {return lastElytraStoreTime;}
    public void setLastElytraStoreTime(long time) {lastElytraStoreTime = time;}

    public double getLastSpeed() {return lastSpeed;}
    public void setLastSpeed(double speed) {lastSpeed = speed;}

    public double getStoredSpeed() {return storedSpeed;}
    public void setStoredSpeed(double speed) {storedSpeed = speed;}
    public double getStoredSpeedVertical() {return storedSpeedVertical;}
    public void setStoredSpeedVertical(double speed) {storedSpeedVertical = speed;}

    public boolean getFlying(){return flying;}
    public void setFlying(boolean flying){this.flying = flying;}

    public long getLastFluidTime() {return lastFluidTime;}

    public BlockState getLastAttachedState(){return lastAttachedState;}
    public double getLastAttachedVelocity(){return lastAttachedVelocity;}

    public void incrementSpeedFlagCount() {speedFlagCount++;}
    public void decrementSpeedFlagCount() {speedFlagCount--; if(speedFlagCount<0) speedFlagCount = 0;}
    public int getSpeedFlagCount() {return speedFlagCount;}

    public void incrementElytraHoverCount() {elytraHoverCount++;}
    public void decrementElytraHoverCount() {elytraHoverCount--; if(elytraHoverCount<0) elytraHoverCount = 0;}
    public int getElytraHoverCount() {return elytraHoverCount;}

    public long getElytraLastRocketTime() {return elytraLastRocketTime;}
    public void setElytraLastRocketTime(long time) {elytraLastRocketTime = time;}

    public void incrementShortSpeedFlagCount() {shortSpeedFlagCount++;}
    public void decrementShortSpeedFlagCount() {shortSpeedFlagCount--; if(shortSpeedFlagCount<0) shortSpeedFlagCount = 0;}
    public int getShortSpeedFlagCount() {return shortSpeedFlagCount;}

    public void incrementUpSpeedFlagCount() {upSpeedFlagCount++;}
    public void decrementUpSpeedFlagCount() {upSpeedFlagCount--; if(upSpeedFlagCount<0) upSpeedFlagCount = 0;}
    public int getUpSpeedFlagCount() {return upSpeedFlagCount;}

    public int getPacketCount(){return packetCount;}
    public boolean getPossibleTimer(){return possibleTimer;}
    public double getCarriedPotential(){return carriedPotential;}

    public double getElytraElevation() {return elytraElevation;}
    public void setElytraElevation(double elevation) {elytraElevation = elevation;}
    public double getElytraMaxElevation() {return elytraMaxElevation;}
    public void setElytraMaxElevation(double elevation) {elytraMaxElevation = elevation;}

    public Vec3d getLastVelocity() {
        return lastVelocity;
    }
    public void setLastVelocity(Vec3d velocity) {
        this.lastVelocity = velocity;
    }

    public boolean getOnGround(){return onGround;}
    public void setOnGround(boolean onGround){this.onGround = onGround;}

    public void setPossibleTimer(boolean timer){this.possibleTimer = timer;}

    public double getSpeedPotential(double timeModifier){
        return (Arrays.stream(speedPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }

    public double getVerticalSpeedPotential(double timeModifier){
        return (Arrays.stream(verticalSpeedPotential).sum() * (timeModifier) * SpeedLimits.FUDGE);
    }

    public boolean getHover() {return hover;}
    public void setHover(boolean hover){this.hover = hover;}


    public void setLastSpeedPotential(double speedPotential) {lastSpeedPotential = speedPotential;}
    public double getLastSpeedPotential() {return lastSpeedPotential;}

    public void moveCurrentToLast(long time){
        if (!changed) return;

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        hasStarted = true;

        changed = false;
        lastCheck = time;
        firstPacketTime = 0;
        Arrays.fill(speedPotential, 0);
        Arrays.fill(verticalSpeedPotential, 0);
        packetCount = 0;
    }

    public void setNew(MovementPacketData packetView, long time) {

        boolean packetChanged = false;

        if (currentX != packetView.getX() ||
        currentY != packetView.getY() ||
        currentZ != packetView.getZ()
        ) {
            packetChanged = true;
        }

        currentX = packetView.getX();
        currentY = packetView.getY();
        currentZ = packetView.getZ();
        changed = true;
        if (firstPacketTime == 0) firstPacketTime = time;
        lastPacketTime = time;
        if (packetChanged) packetCount ++;
    }

    public boolean getStarted() {
        return hasStarted;
    }
    public void setStarted(boolean setStarted) {
        hasStarted = setStarted;
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
        storedSpeed = 0;
        storedSpeedVertical = 0;
        this.player.setVelocity(new Vec3d(0,0,0));
    }

    public void setSpeedPotential(double speed) {
        speedPotential[speedPotentialPointer] = speed;
        speedPotentialPointer++;
        if (speedPotentialPointer > speedPotential.length-1) speedPotentialPointer = 0;
    }


    public void setAverageSpeed(double speed) {
        averageSpeed[averageSpeedPointer] = speed;
        averageSpeedPointer++;
        if (averageSpeedPointer > averageSpeed.length-1) averageSpeedPointer = 0;
    }

    public double getAverageSpeed() {
        return Arrays.stream(averageSpeed).sum() / averageSpeed.length;
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
        hover = false;
    }


    public void setLastAttachedFluid(double x, double y, double z, long time) {
        lastAttachedX = x;
        lastAttachedY = y-1;
        lastAttachedZ = z;
        lastAttachedState = Blocks.AIR.getDefaultState();
        lastAttachedVelocity = 0;
        airTimeStartTime = time;
        lastFluidTime = time;
        //hover = false;
    }

//
    public void addPlayerMovePacket() {playerMovePackets ++;}
    public int getPlayerMovePackets() {return playerMovePackets;}

    public void setLastLevitation(long time) {lastLevitation = time;}
    public long getLastLevitation() {return lastLevitation;}

    public void setOnIce(boolean b) {
        onIce = b;
    }
    public boolean getOnIce(){return onIce;}

    public void setInitial(ServerPlayerEntity player) {
        this.currentX = player.getX();
        this.currentY = player.getY();
        this.currentZ = player.getZ();
        this.lastX = player.getX();
        this.lastY = player.getY();
        this.lastZ = player.getZ();
        this.changed = true;
        this.hasStarted = true;
    }
}
