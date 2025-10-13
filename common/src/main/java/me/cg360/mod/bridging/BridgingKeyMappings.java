package me.cg360.mod.bridging;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.function.Consumer;

import static me.cg360.mod.bridging.BridgingMod.MOD_ID;

public class BridgingKeyMappings {

    private static final ArrayList<KeyMapping> KEY_MAPPINGS = new ArrayList<>();
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(MOD_ID, "category"));

    public static final KeyMapping TOGGLE_BRIDGING = defineMapping(new KeyMapping("key.bridgingmod.toggle_bridging", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, CATEGORY));



    private static KeyMapping defineMapping(KeyMapping k) {
        KEY_MAPPINGS.add(k);
        return k;
    }

    public static void forEachKeybindingDo(Consumer<KeyMapping> keyMappingConsumer) {
        for(KeyMapping mapping: BridgingKeyMappings.KEY_MAPPINGS) {
            keyMappingConsumer.accept(mapping);
        }
    }
}
