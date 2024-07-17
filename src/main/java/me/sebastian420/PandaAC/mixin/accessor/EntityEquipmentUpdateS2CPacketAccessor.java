package me.sebastian420.PandaAC.mixin.accessor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EntityEquipmentUpdateS2CPacket.class)
public interface EntityEquipmentUpdateS2CPacketAccessor {
    @Accessor("equipmentList")
    List<Pair<EquipmentSlot, ItemStack>> getEquipment();

    @Mutable
    @Accessor("equipmentList")
    void setEquipment(List<Pair<EquipmentSlot, ItemStack>> equipmentList);

    @Accessor("id")
    int getEntityId();
}
