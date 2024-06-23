package me.sebastian420.PandaAC.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import me.sebastian420.PandaAC.events.OutgoingPacketListener;
import io.netty.channel.ChannelHandlerContext;
import me.sebastian420.PandaAC.PandaACThread;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    private PacketListener packetListener;
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "channelRead0", at = @At("HEAD"))
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet, CallbackInfo cb) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
            PandaACThread.PACKET_QUEUE.add(new Pair<>(PAPlayer.of(((ServerPlayNetworkHandler)packetListener).player), packet));
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"))
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packetListener instanceof ServerPlayNetworkHandler) {
            OutgoingPacketListener.EVENT.invoker().onOutgoingPacket(PAPlayer.of(((ServerPlayNetworkHandler)packetListener).player), (Packet<ClientPlayPacketListener>) packet);
        }
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo cb) {
        LOGGER.warn(ExceptionUtils.getStackTrace(throwable));
    }
}
