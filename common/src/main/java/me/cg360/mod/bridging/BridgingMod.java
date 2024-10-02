package me.cg360.mod.bridging;

import me.cg360.mod.bridging.config.BridgingConfig;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class BridgingMod {

    public static final String MOD_ID = "bridgingmod";

    public static final ResourceLocation PLACEMENT_ICONS_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/placement_icons.png");

    private static boolean configSuccessfullyInitialized = true;



    public static void init() {
        BridgingConfig.HANDLER.load();
    }


    public static boolean isConfigSuccessfullyInitialized() {
        return configSuccessfullyInitialized;
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(BridgingMod.MOD_ID, name);
    }

    public static BridgingConfig getConfig() {
        return BridgingConfig.HANDLER.instance();
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(BridgingMod.class);
    }
}
