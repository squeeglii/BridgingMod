package me.cg360.mod.bridging.entrypoint.neoforge;

import me.cg360.mod.bridging.config.BridgingConfig;
import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.neoforge.DynamicCrosshairCompat;
import me.cg360.mod.bridging.config.BridgingConfigUI;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
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

    private static final String DYNAMIC_CROSSHAIR_MOD = "dynamiccrosshair";

    public BridgingModNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::registerBindings);
    }


    public void init(FMLClientSetupEvent event) {
        BridgingMod.init();

        if(BridgingMod.isConfigSuccessfullyInitialized())
            ModLoadingContext.get().registerExtensionPoint(
                    IConfigScreenFactory.class,
                    () -> (client, parent) -> BridgingConfigUI.buildConfig().generateScreen(parent)
            );

        if(ModList.get().isLoaded(DYNAMIC_CROSSHAIR_MOD))
            InterModComms.sendTo(DYNAMIC_CROSSHAIR_MOD, "register_api", DynamicCrosshairCompat::new);
    }


    public void registerBindings(RegisterKeyMappingsEvent event) {
        BridgingKeyMappings.forEachKeybindingDo(event::register);
    }

}
