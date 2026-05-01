package me.cg360.mod.bridging.entrypoint.neoforge;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.ModIds;
import me.cg360.mod.bridging.compat.impl.BankStorageCompat;
import me.cg360.mod.bridging.compat.impl.DankStorageCompat;
import me.cg360.mod.bridging.compat.impl.DynamicCrosshairCompat;
import me.cg360.mod.bridging.compat.impl.SableCompat;
import me.cg360.mod.bridging.config.BridgingConfigUI;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = BridgingMod.MOD_ID, dist = Dist.CLIENT)
public class BridgingModNeoForge {

    public BridgingModNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::registerBindings);
    }


    public void init(FMLClientSetupEvent event) {

        if(ModList.get().isLoaded(ModIds.FREE_LOOK))
            BridgingMod.noteIncompatibleMod(ModIds.FREE_LOOK); // this just enables extra compat code. It works.

        BridgingMod.init(); // loads config
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> BridgingConfigUI.buildConfig().generateScreen(parent)
        );

        if(ModList.get().isLoaded(ModIds.DYNAMIC_CROSSHAIR))
            InterModComms.sendTo(ModIds.DYNAMIC_CROSSHAIR, "register_api", DynamicCrosshairCompat::new);

        if(ModList.get().isLoaded(ModIds.DANK_STORAGE)) {
            new DankStorageCompat();
        }

        if(ModList.get().isLoaded(ModIds.BANK_STORAGE)) {
            new BankStorageCompat();
        }

        if(ModList.get().isLoaded(ModIds.SABLE)) {
            new SableCompat().setAsInstance();
        }
    }


    public void registerBindings(RegisterKeyMappingsEvent event) {
        BridgingKeyMappings.forEachKeybindingDo(event::register);
    }

}
