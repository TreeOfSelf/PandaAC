package me.sebastian420.PandaAC.Objects.Data;

import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import me.sebastian420.PandaAC.PacketViews.PlayerMoveC2SPacketView;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerMovementData {

    private double lastX;
    private double lastY;
    private double lastZ;

    private double currentX;
    private double currentY;
    private double currentZ;

    private long lastCheck;

    private final ServerPlayerEntity player;

    public PlayerMovementData(ServerPlayerEntity player){
        currentX = player.getX();
        currentY = player.getY();
        currentZ = player.getZ();
        lastCheck = System.currentTimeMillis();
        this.player = player;
        moveCurrentToLast();
    }

    public double getX(){return currentX;}
    public double getY(){return currentY;}
    public double getZ(){return currentZ;}

    public long getLastCheck(){return lastCheck;}

    public void moveCurrentToLast(){
        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        save();
    }

    public void save(){
        PlayerMovementDataManager.save(this.player, this);
    }

    public void setNew(PlayerMoveC2SPacketView packetView, long time) {
        currentX = packetView.getX();
        currentY = packetView.getY();
        currentZ = packetView.getZ();
        lastCheck = time;
        moveCurrentToLast();
    }
}
