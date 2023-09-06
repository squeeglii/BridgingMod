package me.cg360.mod.bridging;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class BridgingModFabric {

    public void init() {
        BridgingKeyMappings.forEachKeybindingDo(KeyBindingHelper::registerKeyBinding);
        BridgingMod.init();
    }

}
