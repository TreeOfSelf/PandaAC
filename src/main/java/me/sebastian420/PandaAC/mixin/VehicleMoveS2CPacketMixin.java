package me.sebastian420.PandaAC.mixin;

import me.sebastian420.PandaAC.objects.VehicleMoveS2CPacketView;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VehicleMoveS2CPacket.class)
public class VehicleMoveS2CPacketMixin implements VehicleMoveS2CPacketView {
    @Shadow
    private double x;
    @Shadow
    private double y;
    @Shadow
    private double z;
    @Shadow
    private float yaw;
    @Shadow
    private float pitch;

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


}
