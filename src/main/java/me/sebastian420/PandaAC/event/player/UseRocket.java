package me.sebastian420.PandaAC.event.player;

import me.sebastian420.PandaAC.manager.MovementManager;
import me.sebastian420.PandaAC.manager.object.PlayerMovementData;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UseRocket {
    public static void register(){
        UseItemCallback.EVENT.register(UseRocket::onUseItem);
    }

    private static TypedActionResult<ItemStack> onUseItem(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.isFallFlying()) {
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
                PlayerMovementData playerData = MovementManager.getPlayer((ServerPlayerEntity) player);
                int duration = stack.getComponents().get(DataComponentTypes.FIREWORKS).flightDuration();
                switch (duration) {
                    case 1:
                        playerData.setElytraLastRocketTime(System.currentTimeMillis() + 7500);
                        playerData.setElytraMaxElevation(playerData.getY() + 85);
                        break;
                    case 2:
                        playerData.setElytraLastRocketTime(System.currentTimeMillis() + 10000);
                        playerData.setElytraMaxElevation(playerData.getY() + 130);
                        break;
                    case 3:
                        playerData.setElytraLastRocketTime(System.currentTimeMillis() + 15000);
                        playerData.setElytraMaxElevation(playerData.getY() + 170);
                        break;
                }
            }
        }


        return TypedActionResult.pass(stack);

    }
}