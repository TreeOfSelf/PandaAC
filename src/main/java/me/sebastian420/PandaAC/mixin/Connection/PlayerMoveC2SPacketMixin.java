package me.sebastian420.PandaAC.mixin.Connection;

import me.sebastian420.PandaAC.View.PlayerMoveC2SPacketView;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin implements PlayerMoveC2SPacketView {
    @Shadow
    protected double x;
    @Shadow
    protected double y;
    @Shadow
    protected double z;
    @Shadow
    protected float yaw;
    @Shadow
    protected float pitch;
    @Shadow
    protected boolean onGround;
    @Shadow
    protected boolean changePosition;
    @Shadow
    protected boolean changeLook;

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean isOnGround() { return onGround; }

    @Override
    public boolean isChangePosition() {
        return changePosition;
    }

    @Override
    public boolean isChangeLook() {
        return changeLook;
    }
}
