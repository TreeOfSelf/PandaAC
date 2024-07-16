package me.sebastian420.PandaAC.mixin;

import com.mojang.datafixers.util.Pair;
import me.sebastian420.PandaAC.PandaAC;
import io.netty.channel.ChannelHandlerContext;
import me.sebastian420.PandaAC.PandaACThread;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    private PacketListener packetListener;

    @Inject(method = "channelRead0*", at = @At("HEAD"))
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet, CallbackInfo cb) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
            ServerPlayerEntity serverPlayerEntity = ((ServerPlayNetworkHandler) packetListener).getPlayer();
            PandaACThread.PACKET_QUEUE.add(new Pair<>(serverPlayerEntity,packet));
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo cb) {
        PandaAC.LOGGER.warn(ExceptionUtils.getStackTrace(throwable));
    }
}
