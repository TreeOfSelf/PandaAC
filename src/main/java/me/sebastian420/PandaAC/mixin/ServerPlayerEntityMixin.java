package me.sebastian420.PandaAC.mixin;

import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(at = @At("TAIL"), method = "onSpawn", cancellable = true)
    public void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerMovementDataManager.getPlayer(player);
    }
}