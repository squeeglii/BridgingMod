package me.cg360.mod.bridging;

import me.cg360.mod.bridging.compat.forge.DynamicCrosshairCompat;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(BridgingMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BridgingMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BridgingModForge {

    private static final String DYNAMIC_CROSSHAIR_MOD = "dynamiccrosshair";

    @SubscribeEvent
    public void init(FMLClientSetupEvent event) {
        BridgingMod.init();

        if(ModList.get().isLoaded(DYNAMIC_CROSSHAIR_MOD))
            InterModComms.sendTo(DYNAMIC_CROSSHAIR_MOD, "register_api", DynamicCrosshairCompat::new);
    }

}
