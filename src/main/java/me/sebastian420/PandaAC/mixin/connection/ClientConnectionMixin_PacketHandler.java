package me.sebastian420.PandaAC.mixin.connection;

import com.google.gson.internal.reflect.ReflectionHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.object.MovementPacketData;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Arrays;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin_PacketHandler {
    @Shadow
    private PacketListener packetListener;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packetListener instanceof ServerPlayNetworkHandler) {


            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();


            if (!serverPlayerEntity.isAlive()) {
                if (!(packet instanceof ClientStatusC2SPacket)
                && !(packet instanceof CustomPayloadC2SPacket)
                && !(packet instanceof TeleportConfirmC2SPacket)
                && !(packet instanceof PlayerSessionC2SPacket)
                && !(packet instanceof ClientOptionsC2SPacket)
                && !(packet instanceof AcknowledgeChunksC2SPacket)
                ) {
                     ci.cancel();
                }
            } else {
                if (packet instanceof PlayerMoveC2SPacket movePacket) {
                    if (movePacket.changesPosition() &&
                            !(movePacket.getX(serverPlayerEntity.getX()) == 8.5 && (movePacket.getX(serverPlayerEntity.getZ()) == 8.5 ))) {
                        MovementPacketData movementPacketData = new MovementPacketData(movePacket, serverPlayerEntity);
                        PandaACThread.queuePlayerMove(serverPlayerEntity, movementPacketData, System.currentTimeMillis());
                    }
                } else if (packet instanceof VehicleMoveC2SPacket) {
                    MovementPacketData movementPacketData = new MovementPacketData((VehicleMoveC2SPacket) packet);
                    PandaACThread.queueVehicleMove(serverPlayerEntity, movementPacketData, System.currentTimeMillis());
                } else if (packet instanceof UpdateSignC2SPacket signPacket) {
                    String[] text = signPacket.getText();
                    for (int x = 0; x < text.length; x++) {
                        text[x] = text[x].replaceAll("§", "");
                    }
                } else if (packet instanceof BookUpdateC2SPacket bookPacket) {
                    ItemStack bookStack = serverPlayerEntity.getInventory().getStack(bookPacket.slot());
                    PandaLogger.getLogger().info(bookStack);
                    if (bookStack.getItem() != Items.WRITABLE_BOOK) {
                        ci.cancel();
                    }
                }
            }
        }
    }


    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo cb) {
        PandaLogger.getLogger().warn(ExceptionUtils.getStackTrace(throwable));
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"))
    public void send(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
        if (packet instanceof PlayerPositionLookS2CPacket) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            MovementPacketData movementPacketData = new MovementPacketData((PlayerPositionLookS2CPacket) packet);
            PandaACThread.queuePlayerTeleport(serverPlayerEntity, movementPacketData);
        } else if (packet instanceof VehicleMoveS2CPacket) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            MovementPacketData movementPacketData = new MovementPacketData((VehicleMoveS2CPacket) packet);
            PandaACThread.queueServerVehicleMove(serverPlayerEntity, movementPacketData);
        } else if (packet instanceof EntityVelocityUpdateS2CPacket entityVelocityPacket) {

            MovementPacketData movementPacketData = new MovementPacketData(entityVelocityPacket);


            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            if (serverPlayerEntity.getId() == entityVelocityPacket.getEntityId())
                PandaACThread.queuePlayerVelocity(serverPlayerEntity, movementPacketData);
        }

    }
}
