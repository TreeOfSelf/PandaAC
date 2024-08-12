package me.sebastian420.PandaAC.manager;

import me.sebastian420.PandaAC.data.SpeedLimits;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import me.sebastian420.PandaAC.manager.object.VehicleMovementData;
import me.sebastian420.PandaAC.util.BlockUtil;
import me.sebastian420.PandaAC.util.MathUtil;
import me.sebastian420.PandaAC.util.PacketUtil;
import me.sebastian420.PandaAC.util.PandaLogger;
import me.sebastian420.PandaAC.view.PlayerMoveC2SPacketView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.UUID;

public class MovementManager {

    public static HashMap<UUID, PlayerMovementData> playerMovementMap = new HashMap<>();

    public static PlayerMovementData getPlayer(ServerPlayerEntity player) {
        return playerMovementMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerMovementData(player));
    }

    public static void read(ServerPlayerEntity player, PlayerMoveC2SPacket packet, long time) {

        PlayerMoveC2SPacketView packetView = (PlayerMoveC2SPacketView) packet;
        PlayerMovementData playerData = getPlayer(player);

        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);

        int soulSpeed = EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SOUL_SPEED), boots);
        int depthStrider = EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.DEPTH_STRIDER), boots);
        int swiftSneak = EnchantmentHelper.getLevel(player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.SWIFT_SNEAK), leggings);


        //Clear vehicle UUID if not in vehicle
        Entity vehicle = player.getVehicle();
        if (vehicle == null) {
            VehicleMovementData vehicleData = VehicleMovementManager.getPlayer(player);
            vehicleData.setUUID(null);
        }

        ServerWorld world = player.getServerWorld();

        if (packetView.isChangePosition()) {

            double speedPotential;
            double verticalSpeedPotential;

            boolean inFluid = false;
            boolean nearClimbable = PacketUtil.checkClimbable(world, packetView);
            boolean onGround = BlockUtil.checkGround(player, packetView.getY());


            if (!player.isFallFlying()) {
                //Save momentum
                if (playerData.getFlying()) {
                    if (time - playerData.getLastElytraStoreTime() < 1000) {
                        CheckManager.rollBack(player, playerData);
                    } else {
                        playerData.setStoredSpeed(MathUtil.vectorLength(playerData.getLastVelocity().getX(), playerData.getLastVelocity().getZ()) * 500);
                        playerData.setStoredSpeedVertical(Math.abs(playerData.getLastVelocity().getY()) * 500);
                        playerData.setLastElytraStoreTime(time);
                    }
                }
                playerData.setFlying(false);

                if (nearClimbable && !onGround) {
                    verticalSpeedPotential = SpeedLimits.UP_SPEED_CLIMB + Math.abs(player.getVelocity().getY()) * 20;
                } else {
                    verticalSpeedPotential = SpeedLimits.UP_SPEED + Math.abs(player.getVelocity().getY()) * 20;
                }

                BlockState currentFluidState = BlockUtil.checkFluid(player, player.getY());

                if (currentFluidState.getFluidState().isIn(FluidTags.WATER)) {
                    speedPotential = SpeedLimits.SWIM_SPEED_HORIZONTAL_WATER * (1 * depthStrider * 0.5);

                    StatusEffectInstance dolphinsGrace = player.getStatusEffect(StatusEffects.DOLPHINS_GRACE);
                    if (dolphinsGrace != null) speedPotential *= (1 + (double) dolphinsGrace.getAmplifier() / 2);

                    inFluid = true;
                    speedPotential += Math.abs(player.getVelocity().getY());
                } else if (currentFluidState.getFluidState().isIn(FluidTags.LAVA)) {
                    speedPotential = SpeedLimits.SWIM_SPEED_HORIZONTAL_LAVA;
                    inFluid = true;
                    speedPotential += Math.abs(player.getVelocity().getY());
                } else if (onGround) {
                    if (!player.isSneaking() && !player.isCrawling()) {
                        BlockState blockStateUnder = BlockUtil.checkVicinityIce(world, (int) playerData.getX(), (int) playerData.getY(), (int) playerData.getZ());
                        //If they have enough hunger assume they are sprinting
                        if (player.getHungerManager().getFoodLevel() > 6) {
                            //If they are in a 2 block tall passage assume they are jumping
                            if (PacketUtil.checkPassage(world, packetView)) {

                                if (blockStateUnder.isIn(BlockTags.ICE)) {
                                    if (blockStateUnder.getBlock() == Blocks.BLUE_ICE) {
                                        speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE_BLUE_ICE;
                                    } else {
                                        speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE_ICE;
                                    }
                                } else {
                                    speedPotential = SpeedLimits.SPRINT_AND_JUMP_PASSAGE;
                                }
                            } else {
                                //Assume sprint and jumping
                                if (blockStateUnder.isIn(BlockTags.ICE)) {
                                    if (blockStateUnder.getBlock() == Blocks.BLUE_ICE) {
                                        speedPotential = SpeedLimits.SPRINT_ON_BLUE_ICE;
                                    } else {
                                        speedPotential = SpeedLimits.SPRINT_ON_ICE;
                                    }
                                } else {
                                    speedPotential = SpeedLimits.SPRINT_AND_JUMP;
                                }
                            }
                            //Walking
                        } else {
                            speedPotential = SpeedLimits.WALKING;
                        }
                    } else {
                        if (player.isSneaking()) {
                            speedPotential = SpeedLimits.SNEAKING * (1 + swiftSneak * 0.5);
                        } else {
                            speedPotential = SpeedLimits.CRAWLING * (1 + swiftSneak * 0.5);
                        }
                    }
                    playerData.setLastSpeed(speedPotential);
                    //If you are in air, use last speed potential from when you were on the ground
                } else {
                    speedPotential = playerData.getLastSpeed();
                    //Quick fix for jump + sneak
                    if (speedPotential <= SpeedLimits.SNEAKING) speedPotential = SpeedLimits.WALKING_AND_JUMPING;
                }

                if (!inFluid) {
                    if (player.getVelocity().getY() > 0 || Math.abs(player.getVelocity().getY()) < 0.1) {
                        if (!inFluid) {
                            if (BlockUtil.checkVicinityStairs(world, (int) playerData.getX(), (int) playerData.getY(), (int) playerData.getZ())) {
                                verticalSpeedPotential = SpeedLimits.UP_SPEED_STAIRS + Math.abs(player.getVelocity().getY()) * 20;
                                speedPotential *= 1.25;
                            }
                        } else {
                            if (currentFluidState.getFluidState().isIn(FluidTags.WATER)) {
                                verticalSpeedPotential = SpeedLimits.SWIM_SPEED_VERTICAL_WATER_UP  * (1 * depthStrider * 0.5) + Math.abs(player.getVelocity().getY()) * 20;
                                StatusEffectInstance dolphinsGrace = player.getStatusEffect(StatusEffects.DOLPHINS_GRACE);
                                if (dolphinsGrace != null) verticalSpeedPotential *= (1 + (double) dolphinsGrace.getAmplifier() / 2);
                            } else {
                                verticalSpeedPotential = SpeedLimits.SWIM_SPEED_VERTICAL_LAVA_UP + Math.abs(player.getVelocity().getY()) * 20;
                            }
                        }
                    } else {
                        verticalSpeedPotential = Math.abs(player.getVelocity().getY()) * 20;
                    }
                }
            } else {
                speedPotential = MathUtil.vectorLength(player.getVelocity().getX(), player.getVelocity().getZ()) * 20 + SpeedLimits.ELYTRA;
                verticalSpeedPotential = Math.abs(player.getVelocity().getY()) * 20 + SpeedLimits.ELYTRA_VERTICAL;
                playerData.setLastSolidTouch(time);

                if (!playerData.getFlying()) {
                    playerData.setElytraElevation(packetView.getY() + playerData.getStoredSpeedVertical());
                    playerData.setElytraMaxElevation(packetView.getY() + playerData.getStoredSpeedVertical());
                }

                if (playerData.getElytraLastRocketTime() - time > 0) {
                    if (packetView.getY() < playerData.getElytraMaxElevation() && packetView.getY() > playerData.getElytraElevation()) {
                        playerData.setElytraElevation(packetView.getY());
                    }
                }


                playerData.setFlying(true);
            }

            if (player.isUsingRiptide()) {
                playerData.setStoredSpeedVertical(75);
                playerData.setStoredSpeed(75);
                playerData.setAirTimeStartTime(time);
            }


            playerData.setLastVelocity(player.getVelocity());

            StatusEffectInstance jumpBoost = player.getStatusEffect(StatusEffects.JUMP_BOOST);
            StatusEffectInstance levitation = player.getStatusEffect(StatusEffects.LEVITATION);

            int jumpBoostLevel = 1;
            if (jumpBoost != null) jumpBoostLevel = jumpBoost.getAmplifier();

            if (levitation != null) {
                speedPotential = SpeedLimits.LEVITATION_HORIZONTAL;
                verticalSpeedPotential = SpeedLimits.LEVITATION_VERTICAL;
                playerData.setLastLevitation(time);
            }

            if( onGround || nearClimbable) {
                BlockState belowState = PacketUtil.checkBouncyBelow(world, packetView);
                playerData.setLastAttached(packetView.getX(), packetView.getY(), packetView.getZ(), belowState, player.getVelocity().getY(), time);
                playerData.setStoredSpeed(playerData.getStoredSpeed() * 0.75);
                playerData.setStoredSpeedVertical(playerData.getStoredSpeedVertical() * 0.75);
            }else if (time - playerData.getLastSolidTouch() > 1000L * jumpBoostLevel &&
                    packetView.getY() > playerData.getLastY() &&
                    !inFluid && time - playerData.getLastFluidTime() > 500L * jumpBoostLevel &&
                    playerData.getStoredSpeedVertical()<=0 &&
                    time - playerData.getLastLevitation() > 500L) {
                if (!player.isCreative() && !player.isSpectator() && !player.isFallFlying()) CheckManager.rollBack(player ,playerData);
            } else if (inFluid) {
                playerData.setLastAttachedFluid(packetView.getX(), packetView.getY(), packetView.getZ(), time);
                playerData.setStoredSpeed(playerData.getStoredSpeed() * 0.75);
                playerData.setStoredSpeedVertical(playerData.getStoredSpeedVertical() * 0.75);
            }

            double speedMult = 1;
            if (!onGround) speedMult = 1.15;

            double playerMoveLength = MathUtil.vectorLength(player.getMovement().getX(),player.getMovement().getZ());

            StatusEffectInstance speedEffect = player.getStatusEffect(StatusEffects.SPEED);
            if (speedEffect != null) speedPotential *= 1 + (double) speedEffect.getAmplifier() / 2;

            StatusEffectInstance slowEffect = player.getStatusEffect(StatusEffects.SLOWNESS);
            if (onGround && slowEffect != null) {
                double slowChange = 1 - slowEffect.getAmplifier() * 0.075;
                if ( slowChange < 0) slowChange = 0;
                speedPotential *= slowChange;
            }

            if (soulSpeed > 0) {
                if (PacketUtil.checkVicinitySoul(player.getServerWorld(), (int) packetView.getX(), (int) packetView.getY(), (int) packetView.getZ())) {
                    speedPotential *= 1 + (soulSpeed * 0.5);
                }
            }


            if (playerMoveLength > ((speedPotential * SpeedLimits.SHORT_FUDGE + playerData.getStoredSpeed()) / 18)*speedMult) {
                if (!player.isCreative() && !player.isSpectator() && !player.isFallFlying() && !player.isUsingRiptide()) {
                    playerData.incrementShortSpeedFlagCount();
                    if (playerData.getShortSpeedFlagCount() > 6) {
                        PandaLogger.getLogger().info("Flagged Short term speed Speed {} Pot {}",playerMoveLength, ((speedPotential + playerData.getStoredSpeed()) / 18)*speedMult);
                        CheckManager.rollBack(player, playerData);
                    }
                }
            } else {
                playerData.decrementShortSpeedFlagCount();
            }



            playerData.setSpeedPotential(speedPotential);
            playerData.setVerticalSpeedPotential(verticalSpeedPotential);
            playerData.setNew(packetView, time);
        }
    }


    public static void receiveTeleport(ServerPlayerEntity player, PlayerPositionLookS2CPacket teleportData) {
        PlayerMovementData playerData = getPlayer(player);
        playerData.teleport(teleportData.getX(), teleportData.getY(), teleportData.getZ(), System.currentTimeMillis());
    }

    public static void receiveVelocity(ServerPlayerEntity player, EntityVelocityUpdateS2CPacket velocityData) {
        PlayerMovementData playerData = getPlayer(player);
        double prevVelocity = playerData.getStoredSpeed();
        double prevVelocityVertical = playerData.getStoredSpeedVertical();
        playerData.setStoredSpeed(prevVelocity + (Math.abs(velocityData.getVelocityX()) + Math.abs(velocityData.getVelocityZ())) * 40);
        playerData.setStoredSpeedVertical(prevVelocityVertical + Math.abs(velocityData.getVelocityY()) * 40);
    }
}