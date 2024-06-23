package me.sebastian420.PandaAC.modules.movement.elytra;

import me.sebastian420.PandaAC.LoggerThread;
import me.sebastian420.PandaAC.events.PlayerMovementListener;
import me.sebastian420.PandaAC.modules.PAModule;
import me.sebastian420.PandaAC.objects.PlayerMoveC2SPacketView;
import me.sebastian420.PandaAC.objects.entity.PAPlayer;
import me.sebastian420.PandaAC.util.MathUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraMaxHorizontalSpeedCheck extends PAModule implements PlayerMovementListener {
    public ElytraMaxHorizontalSpeedCheck() {
        super("elytra_max_horizontal_check");
        PlayerMovementListener.EVENT.register(this);
    }

    private class ElytraMaxHorizontalSpeedCheckData {
        public double move = 0.0;
        public long starttime = System.currentTimeMillis();

        double lastX,lastY,lastZ;
        long startAfter = System.currentTimeMillis();
    }

	@Override
	public void onMovement(PAPlayer player, PlayerMoveC2SPacketView packet, MoveCause cause) {
        if ( !player.isFallFlying() || cause.isTeleport() || MathUtil.getDistanceSquared(0, 0, player.getVelocity().getX(), player.getVelocity().getZ()) > 4) {
            ElytraMaxHorizontalSpeedCheckData data = player.getData(ElytraMaxHorizontalSpeedCheckData.class);
            if (data != null) {
                data.move = 0;
            }
            return;
        }
        if (!packet.isChangePosition()) return;
        double distance = MathUtil.getDistanceSquared(player.getPacketX(), player.getPacketZ(), packet.getX(), packet.getZ());
        ElytraMaxHorizontalSpeedCheckData data = player.getOrCreateData(ElytraMaxHorizontalSpeedCheckData.class, ElytraMaxHorizontalSpeedCheckData::new);
        data.move += distance;
        if (System.currentTimeMillis() - data.starttime > 2500) {
            //LoggerThread.info("Hor speed:"+data.move);
            if (data.move >= 250 ) {
                if(data.lastX!=0 && data.lastY!=0 && data.lastZ!=0) {

                    if (player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                        ItemStack elytra = player.asMcPlayer().getEquippedStack(EquipmentSlot.CHEST);
                        if (elytra.getDamage() < elytra.getMaxDamage()) {
                            elytra.damage(500, player.asMcPlayer(), EquipmentSlot.CHEST);
                            player.asMcPlayer().sendEquipmentBreakStatus(elytra.getItem(), EquipmentSlot.CHEST);
                        }
                    }
                    LoggerThread.info("ELYTRA HOR ROLLBACK");
                    player.asMcPlayer().teleport(player.getWorld().toServerWorld(),data.lastX,data.lastY,data.lastZ, player.asMcEntity().getYaw(), player.asMcEntity().getPitch());
                }
            }else{
                data.lastX = packet.getX();
                data.lastY = packet.getY();
                data.lastZ = packet.getZ();
            }
            data.starttime = System.currentTimeMillis();
            data.move = 0.0;
        }
	}
}
