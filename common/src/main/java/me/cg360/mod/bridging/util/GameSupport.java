package me.cg360.mod.bridging.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class GameSupport {

    public static final double MAXIMUM_PLACE_REACH = 4.5d;

    public static double getReach() {
        if(Minecraft.getInstance().player == null)
            return MAXIMUM_PLACE_REACH;

        return Minecraft.getInstance().player.blockInteractionRange();
    }

    public static boolean isHoldingPlaceable(Player player) {
        return GameSupport.isStackPlaceable(player.getMainHandItem()) ||
               GameSupport.isStackPlaceable(player.getOffhandItem());
    }

    public static boolean isStackPlaceable(ItemStack stack) {
        if(stack == null) return false;
        return stack.getItem() instanceof BlockItem;
    }

}
