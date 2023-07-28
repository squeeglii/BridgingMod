package me.cg360.mod.bridging;

import com.mojang.logging.LogUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;

public class BridgingMod {

    public static final ResourceLocation PLACEMENT_ICONS_TEXTURE = ResourceLocation.tryBuild("bridgingmod", "textures/gui/placement_icons.png");

    private static boolean configSuccessfullyInitialized = false;
    private static ConfigHolder<BridgingConfig> config = null;


    public void init() {

        try {
            config = AutoConfig.register(BridgingConfig.class, GsonConfigSerializer::new);

            // Block rendering of config if snapshot as that's the most likely thing to break.
            configSuccessfullyInitialized = !SharedConstants.SNAPSHOT;

        } catch (Exception e) {
            LogUtils.getLogger().error("dsjlkhfdshjfhfjsdkhlfjhdslkjhfa");
            e.printStackTrace();
        }
    }


    public static boolean isConfigSuccessfullyInitialized() {
        return configSuccessfullyInitialized;
    }

    public static BridgingConfig getConfig() {
        return config == null ? new BridgingConfig() : config.get();
    }
}
