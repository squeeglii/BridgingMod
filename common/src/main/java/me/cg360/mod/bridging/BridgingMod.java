package me.cg360.mod.bridging;

import dev.isxander.yacl3.platform.YACLPlatform;
import me.cg360.mod.bridging.config.BridgingConfig;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class BridgingMod {

    public static final String MOD_ID = "bridgingmod";

    // Environment -- simple platform indepentent way of tracking incompatible mods.
    private static Set<String> compatibilityNeededMods = new HashSet<>();

    // Not quite incompatible, but some config defaults need to be changed when a mod is detected.
    public static void noteIncompatibleMod(String modId) {
        compatibilityNeededMods.add(modId.toLowerCase());
    }

    public static SourcePerspective getCompatibleSourcePerspective() {
        SourcePerspective cfgPerspective = BridgingMod.getConfig().getPerspectiveLock();

        // if LET_BRIDGING_MOD_DECIDE, figure out if mod compatibility. Otherwise
        // respect user choice.
        if(cfgPerspective != SourcePerspective.LET_BRIDGING_MOD_DECIDE)
            return cfgPerspective;

        return compatibilityNeededMods.contains(ModIds.FREE_LOOK)
                ? SourcePerspective.ALWAYS_EYELINE
                : SourcePerspective.COPY_TOGGLE_PERSPECTIVE;
    }

    public static void init() {
        BridgingConfig.HANDLER.load();
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
