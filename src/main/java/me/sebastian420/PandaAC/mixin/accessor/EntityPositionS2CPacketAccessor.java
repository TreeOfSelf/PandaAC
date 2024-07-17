package me.sebastian420.PandaAC.mixin.accessor;

import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPositionS2CPacket.class)
public interface EntityPositionS2CPacketAccessor {
    @Accessor("x")
    double getX();

    @Accessor("z")
    double getZ();

    @Mutable
    @Accessor("x")
    void setX(double X);

    @Mutable
    @Accessor("z")
    void setZ(double z);
}
