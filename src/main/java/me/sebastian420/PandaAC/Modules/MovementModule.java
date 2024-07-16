package me.sebastian420.PandaAC.Modules;

import me.sebastian420.PandaAC.Objects.Data.PlayerMovementData;
import me.sebastian420.PandaAC.Objects.PlayerMovementDataManager;
import me.sebastian420.PandaAC.Objects.ThreadedWorldManager;
import me.sebastian420.PandaAC.PacketViews.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.PandaAC;
import me.sebastian420.PandaAC.Util.MathUtil;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MovementModule {
    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet) {
        long time = System.currentTimeMillis();
        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;

        if (packetView.isChangePosition()) {
            PlayerMovementData playerData = PlayerMovementDataManager.getPlayer(player);

            long timeDifMs = time - playerData.getLastCheck();
            double distance = MathUtil.getDistance(playerData.getX(), playerData.getZ(), packetView.getX(), packetView.getZ());
            double speedMps = (distance * 1000.0) / timeDifMs;
            PandaAC.LOGGER.info("Raw values - Distance: {} blocks, Time: {} ms", distance, timeDifMs);
            PandaAC.LOGGER.info("Moving at speed: {} m/s", speedMps);

            BlockState blockUnder = ThreadedWorldManager.getWorld(player.getServerWorld()).getBlockState(new BlockPos(player.getBlockPos().offset(Direction.DOWN)));
            PandaAC.LOGGER.info("Block Under: {}", blockUnder);
            playerData.setNew(packetView, time);
        }
    }
}
