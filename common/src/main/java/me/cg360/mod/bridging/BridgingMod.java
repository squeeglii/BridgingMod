package me.cg360.mod.bridging;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class BridgingMod {

    public static final String MOD_ID = "bridgingmod";

    public static final ResourceLocation PLACEMENT_ICONS_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/placement_icons.png");

    private static boolean configSuccessfullyInitialized = true;
    private static Supplier<BridgingConfig> configSource = null;


    public static void init(Supplier<BridgingConfig> bridgingConfigProvider) {
        configSource = bridgingConfigProvider;
    }


    public static boolean isConfigSuccessfullyInitialized() {
        return configSuccessfullyInitialized;
    }

    public static BridgingConfig getConfig() {
        return configSource == null ? new BridgingConfig() : configSource.get();
    }
}
