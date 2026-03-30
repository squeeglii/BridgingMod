package me.cg360.mod.bridging.entrypoint.fabric;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.ModIds;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

//import me.cg360.mod.bridging.compat.impl.BankStorageCompat;
//import me.cg360.mod.bridging.compat.impl.DankStorageCompat;

public class BridgingModFabric {

    public void init() {
        BridgingKeyMappings.forEachKeybindingDo(KeyMappingHelper::registerKeyMapping);
        BridgingMod.init();

        if(FabricLoader.getInstance().isModLoaded(ModIds.FREE_LOOK)) {
            BridgingMod.noteIncompatibleMod(ModIds.FREE_LOOK); // this just enables extra compat code. It works.
        }

        if(FabricLoader.getInstance().isModLoaded(ModIds.DANK_STORAGE)) {
            BridgingMod.getLogger().warn("Dank Storage Compat disabled. Submit an issue with BridgingMod if it is now on 26.1");
            //new DankStorageCompat();
        }

        if(FabricLoader.getInstance().isModLoaded(ModIds.BANK_STORAGE)) {
            BridgingMod.getLogger().warn("Bank Storage Compat disabled. Submit an issue with BridgingMod if it is now on 26.1");
            //new BankStorageCompat();
        }
    }

}
