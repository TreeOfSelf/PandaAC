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

public class UseItem {
    public static void register(){
        UseItemCallback.EVENT.register(UseItem::onUseItem);
    }

    private static TypedActionResult<ItemStack> onUseItem(PlayerEntity player, World world, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.isFallFlying()) {
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
                PlayerMovementData playerData = MovementManager.getPlayer((ServerPlayerEntity) player);
                int duration = stack.getComponents().get(DataComponentTypes.FIREWORKS).flightDuration();
                playerData.setStoredSpeed(500 * duration);
            }
        }

        return TypedActionResult.pass(stack);
    }
}
