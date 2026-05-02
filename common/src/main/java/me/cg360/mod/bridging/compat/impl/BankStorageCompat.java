package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.impl.items.BankStorageItemHandler;
import net.minecraft.resources.ResourceLocation;

public class BankStorageCompat {

    public BankStorageCompat() {
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_1"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_2"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_3"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_4"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_5"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_6"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_7"), BankStorageItemHandler.INSTANCE);
        SpecialHandlers.registerSpecialItemHandler(ResourceLocation.fromNamespaceAndPath("bankstorage", "bank_link"), BankStorageItemHandler.INSTANCE);
    }

}
