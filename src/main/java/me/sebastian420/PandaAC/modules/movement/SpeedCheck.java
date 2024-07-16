package me.sebastian420.PandaAC.modules.movement;

import me.sebastian420.PandaAC.events.OutgoingTeleportListener;
import me.sebastian420.PandaAC.events.PlayerEndTickCallback;
import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.events.PlayerSpawnListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.trackers.Trackers;
import net.minecraft.block.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TODO This is garbage
//TODO Ice
//TODO Velocity/Inertia (not the mod)
//TODO Block on head 
public class SpeedCheck extends PAModule implements PlayerMovementListener, PlayerEndTickCallback, OutgoingTeleportListener, PlayerSpawnListener {

    public boolean hasSpeedPotionEffect(PlayerEntity player) {
        Map<RegistryEntry<StatusEffect>, StatusEffectInstance> effects = player.getActiveStatusEffects();
        for (RegistryEntry<StatusEffect> effect : effects.keySet()) {
            if (effect == StatusEffects.SPEED) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerOnSoulSoil(PlayerEntity player) {
        // Check if player is wearing Soul Speed boots
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        if (EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED),boots) > 0) {
            // Check if player is standing on Soul Sand or Soul Soil
            World world = player.getEntityWorld();
            BlockPos pos = new BlockPos((int) player.getX(), (int)player.getY(), (int)player.getZ());
            BlockState blockState = world.getBlockState(pos);
            BlockPos posTwo = new BlockPos((int) player.getX(), (int)player.getY() - 1, (int)player.getZ());
            BlockState blockStateTwo = world.getBlockState(posTwo);
            return blockState.isOf(Blocks.SOUL_SOIL) || blockState.isOf(Blocks.SOUL_SAND) ||
                    blockStateTwo.isOf(Blocks.SOUL_SOIL) || blockStateTwo.isOf(Blocks.SOUL_SAND);
        }
        return false;
    }

    public boolean isDepthSwimming(PlayerEntity player) {
        if(!player.isSubmergedInWater() || !player.isSwimming()) return false;
        // Check if player is wearing Soul Speed boots
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        StatusEffectInstance dolphinsGrace = player.getStatusEffect(StatusEffects.DOLPHINS_GRACE);

        if (EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.DEPTH_STRIDER),boots) > 0 || (dolphinsGrace != null && dolphinsGrace.getDuration() > 0)) {
           return true;
        }
        return false;
    }
    public static boolean isIceBelowPlayer(PlayerEntity player) {
        World world = player.getEntityWorld();
        BlockPos pos = new BlockPos((int) player.getX(), (int)player.getY() - 1, (int)player.getZ());
        BlockState blockState = world.getBlockState(pos);
        BlockPos posTwo = new BlockPos((int) player.getX(), (int)player.getY() - 2, (int)player.getZ());
        BlockState blockStateTwo = world.getBlockState(posTwo);
        return blockState.isOf(Blocks.ICE) || blockState.isOf(Blocks.PACKED_ICE) || blockState.isOf(Blocks.BLUE_ICE) ||
        blockStateTwo.isOf(Blocks.ICE) || blockStateTwo.isOf(Blocks.PACKED_ICE) || blockStateTwo.isOf(Blocks.BLUE_ICE);
    }

    public static boolean isBlockAbovePlayer(PlayerEntity player) {
        World world = player.getEntityWorld();
        BlockPos pos = new BlockPos((int) player.getX(), (int) (player.getY() + 2), (int) player.getZ());
        return !world.getBlockState(pos).isAir();
    }

    public static boolean isStairs(BlockState state){
        //return state.toString().contains("stairs");
        return state.getBlock() instanceof StairsBlock;
    }
    public static boolean isGoingUpStairs(PlayerEntity player) {
        World world = player.getEntityWorld();

        BlockPos pos = new BlockPos((int) player.getX(), (int)player.getY() - 2, (int)player.getZ());
        BlockState state = world.getBlockState(pos);
        BlockPos posTwo = new BlockPos((int) player.getX(), (int)player.getY() -3, (int)player.getZ());
        BlockState stateTwo = world.getBlockState(posTwo);
        BlockPos posThree = new BlockPos((int) player.getX(), (int)player.getY() -1, (int)player.getZ());
        BlockState stateThree = world.getBlockState(posThree);

        if(isStairs(state)){
            if (isStairs(world.getBlockState(pos.down().west())) ||
                    isStairs(world.getBlockState(pos.down().east())) ||
            isStairs(world.getBlockState(pos.down().north())) ||
            isStairs(world.getBlockState(pos.down().south()))){
                return true;
            }
        }

        if(isStairs(stateTwo)){
            if (isStairs(world.getBlockState(posTwo.down().west())) ||
                    isStairs(world.getBlockState(posTwo.down().east())) ||
                    isStairs(world.getBlockState(posTwo.down().north())) ||
                    isStairs(world.getBlockState(posTwo.down().south()))){
                return true;
            }
        }

        if(isStairs(stateThree)){
            if (isStairs(world.getBlockState(posThree.down().west())) ||
                    isStairs(world.getBlockState(posThree.down().east())) ||
                    isStairs(world.getBlockState(posThree.down().north())) ||
                    isStairs(world.getBlockState(posThree.down().south()))){
                return true;
            }
        }

        return false;
    }


    public SpeedCheck() {
        super("speed_check");
        PlayerMovementListener.EVENT.register(this);
        PlayerEndTickCallback.EVENT.register(this);
        OutgoingTeleportListener.EVENT.register(this);
        PlayerSpawnListener.EVENT.register(this::onSpawn);


    }




    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {





        if(player.asMcPlayer().getY() < -64){
            player.asMcPlayer().damage(player.asMcPlayer().getWorld().getDamageSources().generic(), 4);
        }
        if(player.isFallFlying()){
            SpeedCheckData data = player.getOrCreateData(SpeedCheckData.class, SpeedCheckData::new);

            data.startAfter = System.currentTimeMillis();

            data.time = System.currentTimeMillis();
            data.timeShort = System.currentTimeMillis();
        }
        if (!packet.isChangePosition() || player.isFallFlying() || player.isSpectator()) return;
        SpeedCheckData data = player.getOrCreateData(SpeedCheckData.class, SpeedCheckData::new);
        if (packet.isChangePosition() &&
            !(player.isCreative() || System.currentTimeMillis() - player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport < 1000)
        ) {
            //SHORT

            if(System.currentTimeMillis() - data.timeShort>250) {

                double xdelta = Math.abs(packet.getX() - data.lastXX);
                double zdelta = Math.abs(packet.getZ() - data.lastZZ);
                double dist = Math.sqrt(Math.pow(xdelta,2)+Math.pow(zdelta,2));

                double magicNumber = dist;//(dist / (1 + (player.getSpeed() * 1.2))); // TODO Lmao what is this

                double speedMult = (System.currentTimeMillis() - data.timeShort) / 250;


                double speedValue = 8*0.4;




                if(isIceBelowPlayer(player.asMcPlayer()) && isBlockAbovePlayer(player.asMcPlayer())){
                    speedValue = 28 *0.4;
                }
                else if(System.currentTimeMillis() - data.lastStride < 3000 || isDepthSwimming(player.asMcPlayer())){
                    speedValue = 26*0.4;
                }
                else if(isPlayerOnSoulSoil(player.asMcPlayer())){
                    speedValue = 14 *0.4;
                }
                else if(isBlockAbovePlayer(player.asMcPlayer())){
                    speedValue = 13 *0.4;
                }
                else if(isIceBelowPlayer(player.asMcPlayer())){
                    speedValue = 11 *0.4;
                }
                else if(isGoingUpStairs(player.asMcPlayer())){
                    speedValue = 9.3*0.4;
                }


                if(hasSpeedPotionEffect(player.asMcPlayer())){
                    speedValue *=1.5;
                }


                double speedCheck = speedValue*speedMult ;//3.0 * speedMult;

                //LoggerThread.info("Dist Short: "+String.valueOf(dist)+" , "+speedCheck+" for "+player.asMcPlayer().getName());

                if (magicNumber > speedCheck /*maxSpeedMagicNumber.get()*/ && System.currentTimeMillis() - data.startAfter > 5000 && player.getWorld().getTime() - player.getPistonMovementTick() > 1000 &&
                        System.currentTimeMillis() - player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport > 1000 &&
                        !player.asMcEntity().hasVehicle() && player.asMcPlayer().isAlive()) {
                        player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.rollbackX,Math.round(data.rollbackY),data.rollbackZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                        data.lastRollback = System.currentTimeMillis();
                        data.lastXX = data.rollbackX;
                        data.lastYY = data.rollbackY;
                        data.lastZZ = data.rollbackZ;
                        data.lastX = data.rollbackX;
                        data.lastY = data.rollbackY;
                        data.lastZ = data.rollbackZ;
                        player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport = System.currentTimeMillis() - 1000;


                }else{
                    data.lastXX = packet.getX();
                    data.lastYY = packet.getY();
                    data.lastZZ = packet.getZ();
                }
                data.timeShort = System.currentTimeMillis();

            }



            //LONG


            if(System.currentTimeMillis() - data.time>1000) {

                double xdelta = Math.abs(packet.getX() - data.lastX);
                double zdelta = Math.abs(packet.getZ() - data.lastZ);
                double dist = Math.sqrt(Math.pow(xdelta,2)+Math.pow(zdelta,2));

                double magicNumber = dist;//(dist / (1 + (player.getSpeed() * 1.2))); // TODO Lmao what is this

                double speedMult = (System.currentTimeMillis() - data.time) / 1000;


                double speedValue = 8;



                if(isIceBelowPlayer(player.asMcPlayer()) && isBlockAbovePlayer(player.asMcPlayer())){
                    speedValue = 28;
                }
                else if(System.currentTimeMillis() - data.lastStride < 3000 || isDepthSwimming(player.asMcPlayer())){
                    speedValue = 26;
                }
                else if(isPlayerOnSoulSoil(player.asMcPlayer())){
                    speedValue = 14;
                }
                else if(isBlockAbovePlayer(player.asMcPlayer())){
                    speedValue = 13;
                }
                else if(isIceBelowPlayer(player.asMcPlayer())){
                    speedValue = 11;
                }
                else if(isGoingUpStairs(player.asMcPlayer())){
                    speedValue = 9.3;
                }



                if(hasSpeedPotionEffect(player.asMcPlayer())){
                    speedValue *=1.5;
                }


                double speedCheck = speedValue*speedMult ;//3.0 * speedMult;

                if (magicNumber > speedCheck /*maxSpeedMagicNumber.get()*/ && System.currentTimeMillis() - data.startAfter > 5000 && player.getWorld().getTime() - player.getPistonMovementTick() > 1000 &&
                        System.currentTimeMillis() - player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport > 1000 &&
                        !player.asMcEntity().hasVehicle() && player.asMcPlayer().isAlive()) {

                    data.fastCounter+=1;

                    if(data.fastCounter>1) {
                        player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.rollbackX,Math.round(data.rollbackY),data.rollbackZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                        data.lastXX = data.rollbackX;
                        data.lastYY = data.rollbackY;
                        data.lastZZ = data.rollbackZ;
                        data.lastX = data.rollbackX;
                        data.lastY = data.rollbackY;
                        data.lastZ = data.rollbackZ;
                        data.fastCounter=0;
                        player.getTracked(Trackers.PLAYER_LAST_TELEPORT_TRACKER).lastTeleport = System.currentTimeMillis() - 1000;
                    }else{
                        data.lastX = packet.getX();
                        data.lastY = packet.getY();
                        data.lastZ = packet.getZ();
                    }

                }else{
                    data.lastX = packet.getX();
                    data.lastY = packet.getY();
                    data.lastZ = packet.getZ();
                    if(System.currentTimeMillis() - data.lastRollback > 2000) {
                        data.rollbackX = packet.getX();
                        data.rollbackY = packet.getY();
                        data.rollbackZ = packet.getZ();
                    }
                    data.fastCounter -=1;
                    if(data.fastCounter<0){
                        data.fastCounter = 0;
                    }
                }
                data.time = System.currentTimeMillis();

            }
        }else{
            if(packet.isChangePosition()) {
                data.lastX = packet.getX();
                data.lastY = packet.getY();
                data.lastZ = packet.getZ();
                if(System.currentTimeMillis() - data.lastRollback > 2000) {
                    data.rollbackX = packet.getX();
                    data.rollbackY = packet.getY();
                    data.rollbackZ = packet.getZ();
                }
                data.time = System.currentTimeMillis();
            }
        }

    }

    public static class SpeedCheckData {
        public long lastRollback = 0;
        long time, timeShort = System.currentTimeMillis();

        long startAfter = System.currentTimeMillis();

        double lastX,lastY,lastZ = 0;
        double lastXX, lastYY, lastZZ =0;

        int fastCounter = 0;
        double rollbackX,rollbackY,rollbackZ=0;

        long lastStride = 0;

    }

    @Override
    public void onSpawn(PAPlayer player) {
        SpeedCheckData data = player.getOrCreateData(SpeedCheckData.class, SpeedCheckData::new);
        data.startAfter = System.currentTimeMillis();
        data.lastX = player.asMcPlayer().getX();
        data.lastY = player.asMcPlayer().getY();
        data.lastZ = player.asMcPlayer().getZ();
        data.lastXX = player.asMcPlayer().getX();
        data.lastYY = player.asMcPlayer().getY();
        data.lastZZ = player.asMcPlayer().getZ();
        data.rollbackX = player.asMcPlayer().getX();
        data.rollbackY = player.asMcPlayer().getY();
        data.rollbackZ = player.asMcPlayer().getZ();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            data.lastX = player.asMcPlayer().getX();
            data.lastY = player.asMcPlayer().getY();
            data.lastZ = player.asMcPlayer().getZ();
            data.lastXX = player.asMcPlayer().getX();
            data.lastYY = player.asMcPlayer().getY();
            data.lastZZ = player.asMcPlayer().getZ();
            data.rollbackX = player.asMcPlayer().getX();
            data.rollbackY = player.asMcPlayer().getY();
            data.rollbackZ = player.asMcPlayer().getZ();
            executor.shutdown();
        }, 1500, TimeUnit.MILLISECONDS);
    }

    @Override
    public long getFlagCoolDownMs() {
        return 0;
    }

    @Override
    public void onPlayerEndTick(PAPlayer player) {

    }

    @Override
    public void onOutgoingTeleport(PAPlayer player, PlayerPositionLookS2CPacket packet) {
        SpeedCheckData data = player.getOrCreateData(SpeedCheckData.class, SpeedCheckData::new);
        data.lastX = player.asMcPlayer().getX();
        data.lastY = player.asMcPlayer().getY();
        data.lastZ = player.asMcPlayer().getZ();
        data.lastXX = player.asMcPlayer().getX();
        data.lastYY = player.asMcPlayer().getY();
        data.lastZZ = player.asMcPlayer().getZ();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            data.lastX = player.asMcPlayer().getX();
            data.lastY = player.asMcPlayer().getY();
            data.lastZ = player.asMcPlayer().getZ();
            data.lastXX = player.asMcPlayer().getX();
            data.lastYY = player.asMcPlayer().getY();
            data.lastZZ = player.asMcPlayer().getZ();
            executor.shutdown();
        }, 200, TimeUnit.MILLISECONDS);
    }
}
