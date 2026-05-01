package me.cg360.mod.bridging.util;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.type.SpecialBridgingItemHandler;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseTorchBlock;

import java.util.Optional;

public class GameSupport {

    public static final double TRAPDOOR_HEIGHT = 3/16d;
    public static final double SLAB_HEIGHT = 8/16d;
    public static final double MAXIMUM_PLACE_REACH = 4.5d;

    public static double getReach() {
        if(Minecraft.getInstance().player == null)
            return MAXIMUM_PLACE_REACH;

        return Minecraft.getInstance().player.blockInteractionRange();
    }

    public static boolean isControllerCrouching() {
        if(Minecraft.getInstance().player == null)
            return false;

        return Minecraft.getInstance().player.isCrouching();
    }

    public static boolean isHoldingPlaceable(Player player) {
        return GameSupport.isStackPlaceable(player.getMainHandItem()) ||
               GameSupport.isStackPlaceable(player.getOffhandItem());
    }

    public static boolean isStackPlaceable(ItemStack stack) {
        if(stack == null) return false;

        Optional<SpecialBridgingItemHandler> handler = SpecialHandlers.getSpecialItemHandler(stack);

        if(handler.isPresent()) {
            return handler.get().canBePlaced(stack);
        }

        return passesDefaultPlacementCheck(stack);
    }


    public static boolean passesDefaultPlacementCheck(ItemStack stack) {
        if(!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        if(BridgingMod.getConfig().shouldSkipTorchBridging() && blockItem.getBlock() instanceof BaseTorchBlock) {
            return false;
        }

        return true;
    }

}
