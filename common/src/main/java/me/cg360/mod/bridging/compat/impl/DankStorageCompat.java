package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.handler.DankStorageHandler;
import net.minecraft.resources.ResourceLocation;

public class DankStorageCompat {

    public DankStorageCompat() {
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_1"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_2"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_3"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_4"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_5"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_6"), DankStorageHandler.INSTANCE);
        SpecialHandlers.registerSpecialHandler(new ResourceLocation("dankstorage", "dank_7"), DankStorageHandler.INSTANCE);
    }

}
