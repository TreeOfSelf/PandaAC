package me.sebastian420.PandaAC.mixin.connection;

import io.netty.channel.ChannelHandlerContext;
import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.util.PandaLogger;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin_PacketHandler {
    @Shadow
    private PacketListener packetListener;

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet, CallbackInfo cb) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
            if (packet instanceof PlayerMoveC2SPacket) {
                ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
                PandaACThread.queuePlayerMove(serverPlayerEntity, (PlayerMoveC2SPacket) packet, System.currentTimeMillis());
            }  else if (packet instanceof VehicleMoveC2SPacket) {
                ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
                PandaACThread.queueVehicleMove(serverPlayerEntity, (VehicleMoveC2SPacket) packet, System.currentTimeMillis());
            }
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo cb) {
        PandaLogger.getLogger().warn(ExceptionUtils.getStackTrace(throwable));
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At("HEAD"))
    public void send(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packet instanceof PlayerPositionLookS2CPacket) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            PandaACThread.queuePlayerTeleport(serverPlayerEntity, (PlayerPositionLookS2CPacket) packet);
        } else if (packet instanceof VehicleMoveS2CPacket) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            PandaACThread.queueServerVehicleMove(serverPlayerEntity, (VehicleMoveS2CPacket) packet);
        } else if (packet instanceof EntityVelocityUpdateS2CPacket entityVelocityPacket) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            if (serverPlayerEntity.getId() == entityVelocityPacket.getId())
                PandaACThread.queuePlayerVelocity(serverPlayerEntity, entityVelocityPacket);
        }

    }
}
