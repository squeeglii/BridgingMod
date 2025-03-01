package me.cg360.mod.bridging.compat.handler;

import com.mojang.logging.LogUtils;
import me.cg360.mod.bridging.building.Bridge;
import me.cg360.mod.bridging.compat.SpecialBridgingHandler;
import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.world.ClientData;

/**
 * Literally only needed for slab & torch support. Unboxes the item held by the bank.
 * WARNING: Only load when DankStorage is present.
 */
public class DankStorageHandler implements SpecialBridgingHandler {

    public static final DankStorageHandler INSTANCE = new DankStorageHandler();

    @Override
    public boolean canBePlaced(ItemStack stack) {
        Item item = stack.getItem();

        // TODO: Look for ItemComponents instead of an item type.
        if(!(item instanceof CDankItem)) {
            // not a dank bank. This compatibility class does nothing!
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            LogUtils.getLogger().warn("Blocked using DankStorage compatibility for an unsupported item (%s)!".formatted(id));
            return false;
        }

        if(!CommonUtils.isConstruction(stack))
            return false;

        ItemStack containedStack = ClientData.selectedItem;
        if(containedStack == null || containedStack.isEmpty())
            return false;

        return GameSupport.passesDefaultPlacementCheck(containedStack);
    }

    @Override
    public boolean canBePlacedInWorld(ItemStack stack, Player player, Level level, BlockPos pos, Direction direction) {
        ItemStack containedStack = ClientData.selectedItem;
        return player.mayUseItemAt(pos, direction, containedStack);
    }

    @Override
    public BlockHitResult generatePlacementTarget(ItemStack stack, Player player, Level level, Direction direction, BlockPos pos) {
        ItemStack containedStack = ClientData.selectedItem;
        return Bridge.getDefaultPlaceAssistTarget(containedStack, level, direction, pos);
    }

}
