package me.sebastian420.PandaAC.mixin.illegal_action;

import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.cast.Player;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

/**
 * Sets the status of the GUI to open.
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin_InventoryClose {

    private final Player player = (Player) this;

    /**
     * Sets the GUI open status to true
     * if enabled in config.
     */
    @Inject(
            method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    private void setOpenGui(@Nullable NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir) {
        player.setOpenGui(PandaAC.pandaConfig.main.checkInventoryActions);
    }

    /**
     * Sets the open GUI status to false when
     * the player is teleported between worlds.
     */
    @Inject(
            method = "setServerWorld",
            at = @At("HEAD")
    )
    private void closeGui(ServerWorld world, CallbackInfo ci) {
        player.setOpenGui(false);
    }

    @Inject(method = "closeHandledScreen", at = @At("TAIL"))
    private void closeHandledScreen(CallbackInfo ci) {
        player.setOpenGui(false);
    }
}
