package me.cg360.mod.bridging.entrypoint.fabric;

import me.cg360.mod.bridging.BridgingConfig;
import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class BridgingModFabric {

    public void init() {
        BridgingKeyMappings.forEachKeybindingDo(KeyBindingHelper::registerKeyBinding);
        BridgingMod.init(AutoConfig.register(BridgingConfig.class, GsonConfigSerializer::new));
    }

}
