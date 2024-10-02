package me.cg360.mod.bridging;

import dev.isxander.yacl3.platform.YACLConfig;
import dev.isxander.yacl3.platform.YACLPlatform;
import me.cg360.mod.bridging.config.BridgingConfig;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
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
        return new ResourceLocation(BridgingMod.MOD_ID, name);
    }

    public static BridgingConfig getConfig() {
        return BridgingConfig.HANDLER.instance();
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(BridgingMod.class);
    }

    public static Path getDefaultConfigPath() {
        return YACLPlatform.getConfigDir();
    }
}
