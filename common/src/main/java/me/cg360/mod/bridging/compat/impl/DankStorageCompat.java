package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.impl.items.DankStorageItemHandler;
import net.minecraft.resources.Identifier;

public class DankStorageCompat {

    public DankStorageCompat() {
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_1"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_2"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_3"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_4"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_5"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_6"), DankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(Identifier.fromNamespaceAndPath("dankstorage", "dank_7"), DankStorageItemHandler.INSTANCE);
    }

}
