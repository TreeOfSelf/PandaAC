package me.sebastian420.PandaAC.mixin.connection;

import me.sebastian420.PandaAC.mixin.accessor.ItemEntityAccessor;
import me.sebastian420.PandaAC.mixin.accessor.LivingEntityAccessor;
import me.sebastian420.PandaAC.mixin.accessor.PlayerEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
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

    private static final TrackedData<Float> LIVING_ENTITY_HEALTH = LivingEntityAccessor.getHealth();
    private static final TrackedData<Float> PLAYER_ENTITY_ABSORPTION = PlayerEntityAccessor.getAbsorption();
    private static final TrackedData<ItemStack> ITEM_ENTITY_STACK = ItemEntityAccessor.getSTACK();

    @Inject(method = "send", at = @At("HEAD"))
    private void onSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        // Check if this is a ServerPlayNetworkHandler instance
        if (!(packet instanceof ServerPlayNetworkHandler)) {
            return;
        }

        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        if (packet instanceof EntityTrackerUpdateS2CPacket trackerPacket) {
            List<DataTracker.SerializedEntry<?>> trackedValues = trackerPacket.trackedValues();
            if (trackedValues == null) return;

            Entity entity = player.getWorld().getEntityById(trackerPacket.id());

            if (pandaConfig.packet.removeHealthTags && entity instanceof LivingEntity livingEntity && entity.isAlive() && !(entity instanceof Saddleable)) {
                trackedValues.removeIf(trackedValue -> trackedValue.value() == LIVING_ENTITY_HEALTH);
                trackedValues.removeIf(trackedValue -> trackedValue.value() == PLAYER_ENTITY_ABSORPTION);

                if (pandaConfig.packet.allowedHealthTags.containsKey(entity.getType())) {
                    float percentage = pandaConfig.packet.allowedHealthTags.getFloat(entity.getType());
                    float divider = livingEntity.getMaxHealth() * percentage;

                    Float newHealth = divider <= 1F ? livingEntity.getHealth() :
                            MathHelper.floor((livingEntity.getHealth() - 1F) / divider) * divider + 1F;

                    var fakeEntry = DataTracker.SerializedEntry.of(LIVING_ENTITY_HEALTH, newHealth);
                    trackedValues.add(fakeEntry);
                }
            }  else if (pandaConfig.packet.removeDroppedItemInfo && entity instanceof ItemEntity itemEntity) {
                boolean removed = trackedValues.removeIf(entry -> entry.value() == ITEM_ENTITY_STACK); // Original item
                if (removed) {
                    ItemStack original = itemEntity.getStack();

                    DataTracker.SerializedEntry<ItemStack> fakeEntry = DataTracker.SerializedEntry.of(ITEM_ENTITY_STACK,  new ItemStack(original.getItem()));
                    trackedValues.add(fakeEntry);
                }
            }
        }
    }
}