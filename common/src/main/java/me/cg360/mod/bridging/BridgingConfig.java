package me.cg360.mod.bridging;

import me.cg360.mod.bridging.util.PlacementAxisMode;
import me.cg360.mod.bridging.util.PlacementAxisModeOverride;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bridgingmod")
public class BridgingConfig implements ConfigData {

    @ConfigEntry.Category("feature")
    @ConfigEntry.Gui.Excluded
    private int version = 2;

    @ConfigEntry.Category("feature")
    private boolean enableBridgingAssist = true;
    @ConfigEntry.Category("feature")
    private boolean onlyBridgeWhenCrouched = false;
    @ConfigEntry.Category("feature")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private PlacementAxisMode supportedBridgeAxes = PlacementAxisMode.BOTH;
    @ConfigEntry.Category("feature")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    private PlacementAxisModeOverride supportedBridgeAxesWhenCrouched = PlacementAxisModeOverride.FALLBACK;
    @ConfigEntry.Category("feature")
    private boolean enableSlabAssist = true;
    @ConfigEntry.Category("feature")
    private boolean enableNonSolidReplace = true;
    @ConfigEntry.Category("feature")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
    @ConfigEntry.Gui.Tooltip()
    private int delayPostBridging = 4; // 4 is vanilla - 3 allows for better forward bridging.


    @ConfigEntry.Category("vfx")
    private boolean showCrosshair = true;
    @ConfigEntry.Category("vfx")
    private boolean showOutline = false;
    @ConfigEntry.Category("vfx")
    private boolean showOutlineEvenWhenNotBridging = false;
    @ConfigEntry.Category("vfx")
    @ConfigEntry.Gui.Tooltip()
    private boolean nonBridgeRespectsCrouchRules = true;
    @ConfigEntry.Category("vfx")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    private int outlineColour = 0x66000000;  // aarrggbb


    @ConfigEntry.Category("debug")
    private boolean showDebugHighlight = true;
    @ConfigEntry.Category("debug")
    private boolean showNonBridgingDebugHighlight = false;
    @ConfigEntry.Category("debug")
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



    public void toggleBridgingEnabled() {
        this.enableBridgingAssist = !this.isBridgingEnabled();
    }

}
