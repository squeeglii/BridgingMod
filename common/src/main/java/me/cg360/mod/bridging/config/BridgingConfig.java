package me.cg360.mod.bridging.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.helper.*;
import me.cg360.mod.bridging.util.PlacementAxisMode;
import me.cg360.mod.bridging.util.PlacementAxisModeOverride;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.loader.api.FabricLoader;

@Config(name = "bridgingmod")
public class BridgingConfig extends DefaultValueTracker {

    public static ConfigClassHandler<BridgingConfig> HANDLER = ConfigClassHandler.createBuilder(BridgingConfig.class)
            .id(BridgingMod.id("main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(BridgingMod.MOD_ID + ".json"))
                    .setJson5(true)
                    .build())
            .build();

    public BridgingConfig() {
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }


    @Category("feature")
    @HideInConfigUI
    private int version = 3;

    @Category("feature")
    private boolean enableBridgingAssist = true;
    @Category("feature")
    private boolean onlyBridgeWhenCrouched = false;
    @Category("feature")
    private PlacementAxisMode supportedBridgeAxes = PlacementAxisMode.BOTH;
    @Category("feature")
    private PlacementAxisModeOverride supportedBridgeAxesWhenCrouched = PlacementAxisModeOverride.FALLBACK;
    @Category("feature")
    private boolean enableSlabAssist = true;
    @Category("feature")
    private boolean enableNonSolidReplace = true;
    @Category("feature")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    @ConfigEntry.Gui.Tooltip()
    private int delayPostBridging = 4; // 4 is vanilla - 3 allows for better forward bridging.


    @Category("vfx")
    private boolean showCrosshair = true;
    @Category("vfx")
    private boolean showOutline = false;
    @Category("vfx")
    private boolean showOutlineEvenWhenNotBridging = false;
    @Category("vfx")
    @IncludeDescription
    private boolean nonBridgeRespectsCrouchRules = true;
    @Category("vfx")
    @UseColourPicker
    private int outlineColour = 0x66000000;  // aarrggbb


    @Category("debug")
    private boolean showDebugHighlight = true;
    @Category("debug")
    private boolean showNonBridgingDebugHighlight = false;
    @Category("debug")
    private boolean showDebugTrace = false;

    /** = Fixes = */
    /** Fixes are simple toggles that are a bit too nitpicky for the features tab.*/
    @Category("fixes")
    private boolean skipTorchBridging = true;



    public boolean isBridgingEnabled() {
        return this.enableBridgingAssist;
    }

    public boolean shouldOnlyBridgeWhenCrouched() {
        return this.onlyBridgeWhenCrouched;
    }

    public boolean isSlabAssistEnabled() {
        return this.enableSlabAssist;
    }

    public boolean isNonSolidReplaceEnabled() {
        return this.enableNonSolidReplace;
    }

    public int getDelayPostBridging() {
        return this.delayPostBridging;
    }

    public PlacementAxisMode getSupportedBridgeAxes() {
        return this.supportedBridgeAxes;
    }

    public PlacementAxisModeOverride getSupportedBridgeAxesWhenCrouched() {
        return this.supportedBridgeAxesWhenCrouched;
    }

    public boolean shouldShowCrosshair() {
        return this.showCrosshair;
    }

    public boolean shouldShowOutline() {
        return this.showOutline;
    }

    public boolean shouldShowOutlineEvenWhenNotBridging() {
        return this.showOutlineEvenWhenNotBridging;
    }

    public boolean shouldNonBridgeRespectsCrouchRules() {
        return this.nonBridgeRespectsCrouchRules;
    }

    public int getOutlineColour() {
        return this.outlineColour;
    }


    public boolean shouldShowOutlineInF3() {
        return this.showDebugHighlight;
    }

    public boolean shouldShowNonBridgeOutlineInF3() {
        return this.showNonBridgingDebugHighlight;
    }

    public boolean shouldShowDebugTrace() {
        return this.showDebugTrace;
    }


    public boolean shouldSkipTorchBridging() {
        return this.skipTorchBridging;
    }

    public void toggleBridgingEnabled() {
        this.enableBridgingAssist = !this.isBridgingEnabled();
    }



}
