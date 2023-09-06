package me.cg360.mod.bridging;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.resources.ResourceLocation;

public class BridgingMod {

    public static final String MOD_ID = "bridgingmod";

    public static final ResourceLocation PLACEMENT_ICONS_TEXTURE = ResourceLocation.tryBuild(MOD_ID, "textures/gui/placement_icons.png");

    private static boolean configSuccessfullyInitialized = true;
    private static ConfigHolder<BridgingConfig> config = null;


    public static void init() {
        config = AutoConfig.register(BridgingConfig.class, GsonConfigSerializer::new);
    }


    public static boolean isConfigSuccessfullyInitialized() {
        return configSuccessfullyInitialized;
    }

    public static BridgingConfig getConfig() {
        return config == null ? new BridgingConfig() : config.get();
    }
}
