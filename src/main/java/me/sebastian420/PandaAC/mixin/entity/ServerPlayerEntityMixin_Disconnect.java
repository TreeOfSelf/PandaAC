package me.sebastian420.PandaAC.mixin.entity;

import me.sebastian420.PandaAC.manager.CheckManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_Disconnect {
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(CallbackInfo ci) {
        //long time = System.currentTimeMillis();
        //CheckManager.run((ServerPlayerEntity)(Object)this, time);
    }
}