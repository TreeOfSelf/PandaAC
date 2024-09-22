package me.sebastian420.PandaAC.mixin.connection;

import com.mojang.authlib.GameProfile;
import me.sebastian420.PandaAC.mixin.accessor.LivingEntityAccessor;
import me.sebastian420.PandaAC.mixin.accessor.PlayerEntityAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static me.sebastian420.PandaAC.PandaAC.pandaConfig;

/**
 * Removes health, absorption and stack NBT from various entities.
 **/
@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin_EntityDataPatch {

    @Shadow @Final protected ClientConnection connection;

    @Shadow protected abstract GameProfile getProfile();

    @Shadow @Final protected MinecraftServer server;
    @Unique
    private static final TrackedData<Float> LIVING_ENTITY_HEALTH = LivingEntityAccessor.getHealth();
    @Unique
    private static final TrackedData<Float> PLAYER_ENTITY_ABSORPTION = PlayerEntityAccessor.getAbsorption();


    @Inject(method = "send", at = @At("HEAD"))
    private void onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {


        GameProfile profile = this.getProfile();
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(profile.getId());

        if (player == null) return;

        if (packet instanceof EntityTrackerUpdateS2CPacket trackerPacket) {
            List<DataTracker.SerializedEntry<?>> trackedValues = trackerPacket.trackedValues();
            if (trackedValues == null) return;

            Entity entity = player.getWorld().getEntityById(trackerPacket.id());

            if (pandaConfig.packet.removeHealthTags && entity instanceof LivingEntity && entity.isAlive() && !(entity instanceof Saddleable)) {
                if (entity.getType() == EntityType.PLAYER) {
                    trackedValues.removeIf(trackedValue -> trackedValue.id() == LIVING_ENTITY_HEALTH.id());
                    //trackedValues.removeIf(trackedValue -> trackedValue.id() == PLAYER_ENTITY_ABSORPTION.id());
                }
            }
        }
    }
}