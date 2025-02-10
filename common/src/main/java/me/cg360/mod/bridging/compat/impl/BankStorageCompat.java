package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.handler.BankStorageHandler;
import me.cg360.mod.bridging.compat.handler.PlaceableItemHandler;
import net.minecraft.resources.ResourceLocation;

public class BankStorageCompat {

    public BankStorageCompat() {
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_1"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_2"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_3"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_4"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_5"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_6"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_7"), BankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("bankstorage", "bank_link"), BankStorageHandler.INSTANCE);
    }

}
