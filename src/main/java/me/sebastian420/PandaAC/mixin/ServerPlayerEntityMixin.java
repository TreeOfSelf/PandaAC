package me.sebastian420.PandaAC.mixin;

import me.sebastian420.PandaAC.events.PlayerDamageListener;
import me.sebastian420.PandaAC.events.PlayerSpawnListener;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements PAPlayer {

    @Inject(method = "damage", at = @At("HEAD"))
    void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cb) {
        PlayerDamageListener.EVENT.invoker().onPlayerDamage((PAPlayer)this, source, amount);
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    public void onSpawn(CallbackInfo cb) {
        //this.tickRollback(this.getX(), this.getY(), this.getZ(), true);
        PlayerSpawnListener.EVENT.invoker().onSpawn((PAPlayer)this);
    }

    @Inject(method = "copyFrom", at = @At("HEAD"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo cb) {
        _setMap(PAPlayer.of(oldPlayer)._getMap());
    }
}
