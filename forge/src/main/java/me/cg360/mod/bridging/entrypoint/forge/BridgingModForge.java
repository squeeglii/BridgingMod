package me.cg360.mod.bridging.entrypoint.forge;

import me.cg360.mod.bridging.BridgingConfig;
import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.forge.DynamicCrosshairCompat;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BridgingMod.MOD_ID)
public class BridgingModForge {

    private static final String DYNAMIC_CROSSHAIR_MOD = "dynamiccrosshair";

    public BridgingModForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }


    public void init(FMLClientSetupEvent event) {
        BridgingMod.init(AutoConfig.register(BridgingConfig.class, GsonConfigSerializer::new));

        BridgingKeyMappings.forEachKeybindingDo(ClientRegistry::registerKeyBinding);

        if(BridgingMod.isConfigSuccessfullyInitialized())
            ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
                    new ConfigGuiHandler.ConfigGuiFactory((client, parent) ->
                            AutoConfig.getConfigScreen(BridgingConfig.class, parent).get()
            ));

        if(ModList.get().isLoaded(DYNAMIC_CROSSHAIR_MOD)) {
            InterModComms.sendTo(DYNAMIC_CROSSHAIR_MOD, "register_api", DynamicCrosshairCompat::new);
        }
    }

}
