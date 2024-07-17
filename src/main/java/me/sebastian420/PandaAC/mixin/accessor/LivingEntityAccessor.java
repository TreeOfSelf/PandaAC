package me.sebastian420.PandaAC.mixin.accessor;// Created 2021-30-06T00:07:26

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Ampflower
 * @since ${version}
 **/
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("HEALTH")
    static TrackedData<Float> getHealth() {
        throw new AssertionError("Accessor failed.");
    }
}
