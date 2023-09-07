package me.cg360.mod.bridging.entrypoint.forge;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BridgingMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BridgingModForgeHooks {

    @SubscribeEvent
    public void registerBindings(RegisterKeyMappingsEvent event) {
        BridgingKeyMappings.forEachKeybindingDo(event::register);
    }

}
