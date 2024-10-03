package me.cg360.mod.bridging.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.helper.*;
import me.cg360.mod.bridging.util.PlacementAxisMode;
import me.cg360.mod.bridging.util.PlacementAxisModeOverride;
import me.shedaniel.autoconfig.annotation.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

@Config(name = "bridgingmod")
public class BridgingConfig extends DefaultValueTracker {

    public static ConfigClassHandler<BridgingConfig> HANDLER = ConfigClassHandler.createBuilder(BridgingConfig.class)
            .id(BridgingMod.id("main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(BridgingMod.MOD_ID + ".json"))
                    .setJson5(false)
                    .build())
            .build();

    public BridgingConfig() {
        this.upgrade();
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }


    @SerialEntry @HideInConfigUI
    private int version = 3;

    @Category("feature") @SerialEntry
    private boolean enableBridgingAssist = true;
    @Category("feature") @SerialEntry
    private boolean onlyBridgeWhenCrouched = false;
    @Category("feature") @SerialEntry
    private PlacementAxisMode supportedBridgeAxes = PlacementAxisMode.BOTH;
    @Category("feature") @SerialEntry
    private PlacementAxisModeOverride supportedBridgeAxesWhenCrouched = PlacementAxisModeOverride.FALLBACK;
    @Category("feature") @SerialEntry
    private boolean enableSlabAssist = true;
    @Category("feature") @SerialEntry
    private boolean enableNonSolidReplace = true;
    @Category("feature") @SerialEntry
    @DiscreteRange(min = 0, max = 20)
    @IncludeDescription(extraParagraphs = 2)
    private int delayPostBridging = 4; // 4 is vanilla - 3 allows for better forward bridging.


    @Category("vfx") @SerialEntry
    private boolean showCrosshair = true;
    @Category("vfx") @SerialEntry
    private boolean showOutline = false;
    @Category("vfx") @SerialEntry
    private boolean showOutlineEvenWhenNotBridging = false;
    @Category("vfx") @SerialEntry
    @IncludeDescription(extraParagraphs = 1)
    private boolean nonBridgeRespectsCrouchRules = true;
    @Category("vfx") @SerialEntry
    private Color outlineColour = new Color(0, 0, 0, 0.4f);


    /** = Fixes = */
    /** Fixes are simple toggles that are a bit too nitpicky for the features tab.*/
    @Category("fixes") @SerialEntry
    @IncludeDescription
    private boolean skipTorchBridging = true;


    @Category("debug") @SerialEntry
    private boolean showDebugHighlight = true;
    @Category("debug") @SerialEntry
    private boolean showNonBridgingDebugHighlight = false;
    @Category("debug") @SerialEntry
    private boolean showDebugTrace = false;





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

    public Color getOutlineColour() {
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
        BridgingConfig.HANDLER.save();
    }

    public void upgrade() {
        this.version = 3;
    }


}
