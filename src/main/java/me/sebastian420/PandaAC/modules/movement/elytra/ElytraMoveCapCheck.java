package me.sebastian420.PandaAC.modules.movement.elytra;

import me.sebastian420.PandaAC.events.ClientCommandC2SPacketListener;
import me.sebastian420.PandaAC.events.InteractItemListener;
import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.events.PlayerSpawnListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.world.Heightmap.Type.WORLD_SURFACE;

/**
 * Based on max distance / fall speed from https://minecraft.gamepedia.com/Elytra#Speed_and_altitude
 * Simple cap so cheats can't pull too many shenanigans
 * TODO: Water
 */
public class ElytraMoveCapCheck extends PAModule implements ClientCommandC2SPacketListener, PlayerMovementListener, InteractItemListener, PlayerSpawnListener {
    public ElytraMoveCapCheck() {
        super("elytra_move_cap");
        ClientCommandC2SPacketListener.EVENT.register(this);
        PlayerMovementListener.EVENT.register(this);
        InteractItemListener.EVENT.register(this);
        PlayerSpawnListener.EVENT.register(this::onSpawn);
    }

    @Override
    public void onSpawn(PAPlayer player) {
        ElytraMoveCapCheckData data = player.getOrCreateData(ElytraMoveCapCheckData.class, ElytraMoveCapCheckData::new);
        if(voidCheck(player)){
            data.voidDamage=500;
        }
    }

    public static class ElytraMoveCapCheckData {
        double yCap = Double.MAX_VALUE;
        boolean isActive = false;
        double lastUpdate = 0;

        double lastX, lastY, lastZ;
        long startAfter = System.currentTimeMillis();

        double voidDamage = 0;
    }

    @Override
    public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        /*GlideCheck.GlideCheckData glideData = player.getData(GlideCheck.GlideCheckData.class);
        if(glideData!=null) {
            glideData.startAfter = System.currentTimeMillis() + 1500;
            glideData.lastX = packet.getX();
            glideData.lastX = packet.getY();
            glideData.lastX = packet.getZ();
        }*/

        ElytraMoveCapCheckData data = player.getOrCreateData(ElytraMoveCapCheckData.class, ElytraMoveCapCheckData::new);
        if (packet.isChangePosition()) {

            double dist = Math.abs(packet.getX() - player.asMcPlayer().getX()) +
                    Math.abs(packet.getY() - player.asMcPlayer().getY()) +
                    Math.abs(packet.getZ() - player.asMcPlayer().getZ());


            //General damage

            if (dist > 0.2) {
                if (player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    ItemStack elytra = player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST);
                    if (elytra.getDamage() < elytra.getMaxDamage()) {

                        int chance = 10;
                        if (ThreadLocalRandom.current().nextInt(chance) == 0) { // 1 in 100 chance
                            elytra.damage(1, player.asMcPlayer(),EquipmentSlot.CHEST);
                        }
                    }
                }
            }

            //Void damage
            if(data!=null) {
                if (player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    ItemStack elytra = player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST);
                    if (elytra.getDamage() < elytra.getMaxDamage()) {
                        if (voidCheck(player)) {
                            data.voidDamage += 0.08;
                            //LoggerThread.info(String.valueOf(data.voidDamage));
                            elytra.damage((int) data.voidDamage, player.asMcPlayer(),EquipmentSlot.CHEST);
                        } else {
                            data.voidDamage = 0;
                        }
                    }
                }
            }

        }

        if (data != null) {
            if (data.isActive && !player.isFallFlying()) {
                data.isActive = false;
            } else if (packet.isChangePosition() && data.isActive) {
                // LoggerThread.info("speed:"+packet.getY()+","+data.yCap);
                if (packet.getY() > data.yCap) {
                    if (data.lastX != 0 && data.lastY != 0 && data.lastZ != 0) {


                        if (player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                            ItemStack elytra = player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST);
                            if (elytra.getDamage() < elytra.getMaxDamage()) {
                                elytra.damage(500, player.asMcPlayer(), EquipmentSlot.CHEST);
                            }
                        }
                        player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.lastX,data.lastY,data.lastZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                    }
                } else if (System.currentTimeMillis() - data.lastUpdate >= 1000) {
                    data.yCap -= 1.5;
                    data.lastUpdate = System.currentTimeMillis();
                    data.lastX = packet.getX();
                    data.lastY = packet.getY();
                    data.lastZ = packet.getZ();
                }
            }
        }
    }

    @Override
    public void onClientCommandC2SPacket(PAPlayer player, ClientCommandC2SPacket packet) {
        if (packet.getMode() == Mode.START_FALL_FLYING && !player.isFallFlying()) {
            ElytraMoveCapCheckData data = player.getOrCreateData(ElytraMoveCapCheckData.class, ElytraMoveCapCheckData::new);
            data.yCap = player.getY() + player.getMaxJumpHeight() + 60 + Math.abs(player.asMcPlayer().getVelocity().y*10); // Give some slack
            data.lastUpdate = System.currentTimeMillis();
            data.isActive = true;
        }
    }


    @Override
    public void onInteractItem(PAPlayer player, Hand hand, ItemStack stackInHand) {
        ElytraMoveCapCheckData data = player.getData(ElytraMoveCapCheckData.class);
        if (data != null && data.isActive && stackInHand.getItem() == Items.FIREWORK_ROCKET) {
            data.yCap += 125; //Bruh
        }
    }

    public boolean voidCheck(PAPlayer pplayer) {

        PlayerEntity player = pplayer.asMcPlayer();
        World world = player.getWorld();
// get player's position
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

// check if there are any non-empty blocks within the specified radius around the player
        for (int i = (int) (x - 32); i <= (int) (x + 32); i++) {
            for (int k = (int) (z - 32); k <= (int) (z + 32); k++) {
                WorldChunk chunk = (WorldChunk) world.getChunk(new BlockPos((int) i, 0, (int) k));
                int highestBlock = chunk.sampleHeightmap(WORLD_SURFACE, (int) i, (int) k);



                if (highestBlock != -65) {
                    return false; // non-empty block found, return false
                }
            }

        }
        return true; // no non-empty blocks found, player is flying in empty space
    }
}
