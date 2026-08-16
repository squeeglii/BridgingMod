package me.cg360.mod.bridging.entrypoint.forge;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.ui.forge.BridgingConfigForgeScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(BridgingMod.MOD_ID)
public class BridgingModForge {

    public BridgingModForge() {
        if (FMLEnvironment.dist != Dist.CLIENT)
            return;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::registerBindings);

    ModLoadingContext.get().registerExtensionPoint(
        ConfigScreenHandler.ConfigScreenFactory.class,
        () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new BridgingConfigForgeScreen(parent))
    );

    MinecraftForge.EVENT_BUS.register(new BridgingForgeClientHooks());
    }


    public void init(FMLClientSetupEvent event) {
        BridgingMod.init();
    }


    public void registerBindings(RegisterKeyMappingsEvent event) {
        BridgingKeyMappings.forEachKeybindingDo(event::register);
    }

}
