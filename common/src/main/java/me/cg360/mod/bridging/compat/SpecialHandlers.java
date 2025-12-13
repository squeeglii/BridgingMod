package me.cg360.mod.bridging.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SpecialHandlers {

    // Item IDs have priority over groups. If an item has its own handler, it's more likely that
    // that's the intended handler.
    private static HashMap<Identifier, SpecialBridgingHandler> specialHandlers = new HashMap<>();

    // Run through all the activation conditions.
    private static LinkedList<SpecialGroupHandlerEntry> specialHandlerGroups = new LinkedList<>();

    // If there's a block that isn't handled by slab assist but should be,
    // add a filter to the list.
    //TODO: Implement as a SpecialBridgingHandler
    public static List<Function<Block, Boolean>> slabAssistFilters = new LinkedList<>();
    static {
        slabAssistFilters.add(block -> block instanceof SlabBlock);
        slabAssistFilters.add(block -> block instanceof TrapDoorBlock);
    }


    public static void registerSpecialHandler(Identifier itemId, SpecialBridgingHandler handler) {
        specialHandlers.put(itemId, handler);
    }

    /* Items must be registered before this is possible. */
    public static void registerSpecialHandler(Item item, SpecialBridgingHandler handler) {
        Identifier itemKey = BuiltInRegistries.ITEM.getKey(item);
        specialHandlers.put(itemKey, handler);
    }

    public static void registerSpecialGroupHandler(SpecialBridgingHandler handler, GroupSelector item) {
        SpecialGroupHandlerEntry entry = new SpecialGroupHandlerEntry(handler, item);
        specialHandlerGroups.add(entry);
    }


    public static Optional<SpecialBridgingHandler> getSpecialHandler(ItemStack itemStack) {
        Identifier itemKey = BuiltInRegistries.ITEM.getKey(itemStack.getItem());

        if(specialHandlers.containsKey(itemKey)) {
            return Optional.of(specialHandlers.get(itemKey));
        }

        // If there's a group handler which has a selector that cover's this item, return that.
        // otherwise, nothing! :D
        return specialHandlerGroups.stream()
                .filter(groupHandler -> groupHandler.groupSelector.passes(itemStack))
                .findFirst()
                .map(entry -> entry.handler);
    }

    public static boolean hasSpecialHandler(ItemStack item) {
        Identifier itemKey = BuiltInRegistries.ITEM.getKey(item.getItem());

        if(specialHandlers.containsKey(itemKey))
            return true;

        return specialHandlerGroups.stream()
                .map(SpecialGroupHandlerEntry::groupSelector)
                .anyMatch(selector -> selector.passes(item));
    }

    // default handlers.
    static {

        // buckets should be placeable!  -- unfortunately, the bucket item uses player pov *inside* the item useOn.
        //                                  so I think the server side would need to match. pls test.
        //registerSpecialGroupHandler(PlaceableItemHandler.INSTANCE, item -> {
        //    return item.getItem() instanceof BucketItem;
        //});

        // Compatibility - Storage mods.
    }

    // Group selector is NOT the placement condition.
    // Group selector just checks whether a block should have special rules *CONSIDERED*.
    public record SpecialGroupHandlerEntry(SpecialBridgingHandler handler, GroupSelector groupSelector) {}

}
