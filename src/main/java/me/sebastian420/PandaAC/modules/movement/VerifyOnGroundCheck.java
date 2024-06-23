package me.sebastian420.PandaAC.modules.movement;

import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.util.BlockCollisionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

//TODO: Enforce fall damage ourselves
public class VerifyOnGroundCheck extends PAModule implements PlayerMovementListener {
    public VerifyOnGroundCheck() {
        super("verify_on_ground_check");
        PlayerMovementListener.EVENT.register(this);
    }

    public boolean solidBlockCheck(Entity playerEntity) {
        World world = playerEntity.getEntityWorld();
        BlockPos[] blockPositions = new BlockPos[] {
                new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 1), (int) playerEntity.getZ()),
                new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 2), (int) playerEntity.getZ()),
                new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 3), (int) playerEntity.getZ()),
                new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 4), (int) playerEntity.getZ())
        };
        for (BlockPos blockPos : blockPositions) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState != Blocks.AIR.getDefaultState()) {
                return true;
            }
        }
        return false;
    }

    public boolean blockUnder(Entity playerEntity) {
        World world = playerEntity.getEntityWorld();
        BlockPos blockPos = new BlockPos((int) playerEntity.getX(), (int) (playerEntity.getY() - 1), (int) playerEntity.getZ());
        BlockState blockUnder = world.getBlockState(blockPos);
        return blockUnder != Blocks.AIR.getDefaultState();
    }



    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        if (cause.isTeleport() || player.isSpectator() || player.getWorld().getTime() - player.getPistonMovementTick() < 1000) return;
        if (BlockCollisionUtil.isNearby(player, 5, 5, BlockCollisionUtil.BOUNCY)) return;


        VerifyOnGroundCheck.onGroundData data = player.getOrCreateData(VerifyOnGroundCheck.onGroundData.class, VerifyOnGroundCheck.onGroundData::new);

        if (packet.isChangePosition()) {
            if (packet.isOnGround()) {

                if(!solidBlockCheck(player.asMcEntity())) {
                    player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.lastX,data.lastY,data.lastZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                }

                else if(blockUnder(player.asMcEntity())){
                    data.lastX = packet.getX();
                    data.lastY = packet.getY();
                    data.lastZ = packet.getZ();
                }
            }
        } else if (packet.isOnGround() && !player.isOnGround()) {
            // Not having a smaller feetbox shouldn't matter unless head is in a block lol
            if(!solidBlockCheck(player.asMcEntity())) {
                player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.lastX,data.lastY,data.lastZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
            }

            else if(blockUnder(player.asMcEntity())){
                data.lastX = packet.getX();
                data.lastY = packet.getY();
                data.lastZ = packet.getZ();
            }
        }
    }


    public class onGroundData {
        AtomicInteger violations = new AtomicInteger(0);
        boolean isActive = false;
        public long enableAfter = 0;

        double lastX,lastY,lastZ;




    }
}

