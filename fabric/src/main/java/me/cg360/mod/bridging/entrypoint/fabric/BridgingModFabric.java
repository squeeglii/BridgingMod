package me.cg360.mod.bridging.entrypoint.fabric;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.ModIds;
import me.cg360.mod.bridging.compat.impl.SableCompat;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;

import me.cg360.mod.bridging.compat.impl.BankStorageCompat;
import me.cg360.mod.bridging.compat.impl.DankStorageCompat;

public class BridgingModFabric {

    public void init() {
        BridgingKeyMappings.forEachKeybindingDo(KeyBindingHelper::registerKeyBinding);
        BridgingMod.init();

        if(FabricLoader.getInstance().isModLoaded(ModIds.FREE_LOOK)) {
            BridgingMod.noteIncompatibleMod(ModIds.FREE_LOOK); // this just enables extra compat code. It works.
        }

        if(FabricLoader.getInstance().isModLoaded(ModIds.DANK_STORAGE)) {
            new DankStorageCompat();
        }

        if(FabricLoader.getInstance().isModLoaded(ModIds.BANK_STORAGE)) {
            new BankStorageCompat();
        }

        if(FabricLoader.getInstance().isModLoaded(ModIds.SABLE)) {
            new SableCompat();
        }
    }

}
