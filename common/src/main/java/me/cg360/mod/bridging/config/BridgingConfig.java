package me.cg360.mod.bridging.config;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.helper.*;
import me.cg360.mod.bridging.util.PlacementAxisMode;
import me.cg360.mod.bridging.util.PlacementAxisModeOverride;

import java.awt.*;

public class BridgingConfig extends DefaultValueTracker {

    public static SimpleConfigHandler<BridgingConfig> HANDLER = new SimpleConfigHandler<>(
            SimpleConfigHandler.resolveDefaultPath(BridgingMod.MOD_ID),
            BridgingConfig.class,
            BridgingConfig::new
    );

    public BridgingConfig() {
        this.upgrade();
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }


    @HideInConfigUI
    private int version = 3;

    @Category("feature")
    @IncludeAnimatedImage("textures/gui/config/bridging.webp")
    @IncludeExtraDescription
    private boolean enableBridgingAssist = true;
    @Category("feature")
    @IncludeExtraDescription
    private boolean onlyBridgeWhenCrouched = false;
    @Category("feature")
    private PlacementAxisMode supportedBridgeAxes = PlacementAxisMode.BOTH;
    @Category("feature")
    private PlacementAxisModeOverride supportedBridgeAxesWhenCrouched = PlacementAxisModeOverride.FALLBACK;
    @Category("feature")
    @IncludeExtraDescription(extraParagraphs = 2)
    @DiscreteRange(min = 0, max = 20)
    private int delayPostBridging = 4; // 4 is vanilla - 3 allows for better forward bridging.


    @Category("vfx")
    @IncludeImage("textures/gui/config/show_crosshair.png")
    private boolean showCrosshair = true;
    @Category("vfx")
    @IncludeImage("textures/gui/config/bridging_outline.png")
    private boolean showOutline = false;
    @Category("vfx")
    @IncludeImage("textures/gui/config/non_bridging_outline.png")
    private boolean showOutlineEvenWhenNotBridging = false;
    @Category("vfx")
    @IncludeExtraDescription
    private boolean nonBridgeRespectsCrouchRules = true;
    @Category("vfx")
    @IncludeImage("textures/gui/config/outline_colour.png")
    @IncludeExtraDescription
    private Color outlineColour = new Color(0, 0, 0, 0.4f);


    /* = Fixes = */
    /* Fixes are simple toggles that are a bit too nitpicky for the features tab.*/
    @Category("fixes")
    private boolean skipTorchBridging = true;
    @Category("fixes")
    @IncludeExtraDescription(extraParagraphs = 3)
    private boolean enableSlabAssist = true;
    @Category("fixes")
    private boolean enableNonSolidReplace = true;


    @Category("debug")
    private boolean showDebugHighlight = true;
    @Category("debug")
    private boolean showNonBridgingDebugHighlight = false;
    @Category("debug")
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

    public void setBridgingEnabled(boolean enableBridgingAssist) {
        this.enableBridgingAssist = enableBridgingAssist;
    }

    public void setOnlyBridgeWhenCrouched(boolean onlyBridgeWhenCrouched) {
        this.onlyBridgeWhenCrouched = onlyBridgeWhenCrouched;
    }

    public void setShowCrosshair(boolean showCrosshair) {
        this.showCrosshair = showCrosshair;
    }

    public void setShowOutline(boolean showOutline) {
        this.showOutline = showOutline;
    }

    public void setShowOutlineEvenWhenNotBridging(boolean showOutlineEvenWhenNotBridging) {
        this.showOutlineEvenWhenNotBridging = showOutlineEvenWhenNotBridging;
    }

    public void save() {
        BridgingConfig.HANDLER.save();
    }

    public void toggleBridgingEnabled() {
        this.enableBridgingAssist = !this.isBridgingEnabled();
        this.save();
    }

    public void upgrade() {
        this.version = 3;
    }


}
