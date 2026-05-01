package me.cg360.mod.bridging.compat;

import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.compat.type.SpecialBridgingItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.*;
import java.util.function.Function;

public class SpecialHandlers {

    // Item IDs have priority over groups. If an item has its own handler, it's more likely that
    // that's the intended handler.
    private static HashMap<ResourceLocation, SpecialBridgingItemHandler> specialSingleItemHandlers = new HashMap<>();

    // Run through all the activation conditions.
    private static LinkedList<SpecialGroupHandlerEntry> specialItemGroupHandlers = new LinkedList<>();

    // And these apply differently to items - run through and match the first that gives an override.
    private static LinkedList<SpecialBridgingEnvironmentHandler> specialEnvironmentHandlers = new LinkedList<>();


    // If there's a block that isn't handled by slab assist but should be,
    // add a filter to the list.
    //TODO: Implement as a SpecialBridgingHandler
    public static List<Function<Block, Boolean>> slabAssistFilters = new LinkedList<>();
    static {
        slabAssistFilters.add(block -> block instanceof SlabBlock);
        slabAssistFilters.add(block -> block instanceof TrapDoorBlock);
    }


    public static void registerSpecialItemHandler(ResourceLocation itemId, SpecialBridgingItemHandler handler) {
        specialSingleItemHandlers.put(itemId, handler);
    }

    /* Items must be registered before this is possible. */
    public static void registerSpecialItemHandler(Item item, SpecialBridgingItemHandler handler) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
        specialSingleItemHandlers.put(itemKey, handler);
    }

    public static void registerSpecialItemGroupHandler(SpecialBridgingItemHandler handler, GroupSelector item) {
        SpecialGroupHandlerEntry entry = new SpecialGroupHandlerEntry(handler, item);
        specialItemGroupHandlers.add(entry);
    }

    public static void registerSpecialEnvironmentHandler(SpecialBridgingEnvironmentHandler handler) {
        specialEnvironmentHandlers.add(handler);
    }


    public static Optional<SpecialBridgingItemHandler> getSpecialItemHandler(ItemStack itemStack) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        if(specialSingleItemHandlers.containsKey(itemKey)) {
            return Optional.of(specialSingleItemHandlers.get(itemKey));
        }

        // If there's a group handler which has a selector that cover's this item, return that.
        // otherwise, nothing! :D
        return specialItemGroupHandlers.stream()
                .filter(groupHandler -> groupHandler.groupSelector.passes(itemStack))
                .findFirst()
                .map(entry -> entry.handler);
    }

    public static List<SpecialBridgingEnvironmentHandler> getSpecialEnvironmentHandlers() {
        return Collections.unmodifiableList(specialEnvironmentHandlers);
    }

    public static boolean hasSpecialItemHandler(ItemStack item) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item.getItem());

        if(specialSingleItemHandlers.containsKey(itemKey))
            return true;

        return specialItemGroupHandlers.stream()
                .map(SpecialGroupHandlerEntry::groupSelector)
                .anyMatch(selector -> selector.passes(item));
    }

    // Group selector is NOT the placement condition.
    // Group selector just checks whether a block should have special rules *CONSIDERED*.
    public record SpecialGroupHandlerEntry(SpecialBridgingItemHandler handler, GroupSelector groupSelector) {}

}
