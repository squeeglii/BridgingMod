package me.cg360.mod.bridging;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class BridgingKeyMappings {

    private static final ArrayList<KeyMapping> KEY_MAPPINGS = new ArrayList<>();


    public static final KeyMapping TOGGLE_BRIDGING = defineMapping(new KeyMapping("key.bridgingmod.toggle_bridging", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, "key.categories.bridgingmod"));



    private static KeyMapping defineMapping(KeyMapping k) {
        KEY_MAPPINGS.add(k);
        return k;
    }

    public static void registerAll() {
        for(KeyMapping mapping: BridgingKeyMappings.KEY_MAPPINGS) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }
}
