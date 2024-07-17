package me.sebastian420.PandaAC.mixin.Entity;

import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class OnSpawnMixin {
    @Inject(at = @At("TAIL"), method = "onSpawn")
    public void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerMovementDataManager.getPlayer(player);
    }
}