package me.sebastian420.PandaAC.mixin.movement;

import me.sebastian420.PandaAC.PandaAC;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin_NetherRoofDamage {

    @Shadow
    protected abstract void tickInVoid();

    @Unique
    private final Entity self = (Entity) (Object) this;

    @Inject(method = "attemptTickInVoid", at = @At("RETURN"))
    private void inflictRoofDamage(CallbackInfo ci) {
        if (PandaAC.pandaConfig.main.inflictNetherRoofDamage != -1 &&
                self.getY() >= PandaAC.pandaConfig.main.inflictNetherRoofDamage &&
                self.getEntityWorld().getRegistryKey() == World.NETHER) {
            this.tickInVoid();
        }
    }
}
