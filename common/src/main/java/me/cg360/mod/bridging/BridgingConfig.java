package me.cg360.mod.bridging;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bridgingmod")
public class BridgingConfig implements ConfigData {

    @ConfigEntry.Category("feature")
    private boolean enableBridgingAssist = true;
    @ConfigEntry.Category("feature")
    private boolean onlyBridgeWhenCrouched = false;
    @ConfigEntry.Category("feature")
    private boolean enableSlabAssist = true;
    @ConfigEntry.Category("feature")
    private boolean enableNonSolidReplace = true;


    @ConfigEntry.Category("vfx")
    private boolean showCrosshair = true;
    @ConfigEntry.Category("vfx")
    private boolean showOutline = false;
    @ConfigEntry.Category("vfx")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    private int outlineColour = 0x66000000;  // aarrggbb


    @ConfigEntry.Category("debug")
    private boolean showDebugHighlight = true;
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


    public boolean shouldShowCrosshair() {
        return this.showCrosshair;
    }

    public boolean shouldShowOutline() {
        return this.showOutline;
    }

    public int getOutlineColour() {
        return this.outlineColour;
    }


    public boolean shouldShowOutlineInF3() {
        return this.showDebugHighlight;
    }

    public boolean shouldShowDebugTrace() {
        return this.showDebugTrace;
    }



    public void toggleBridgingEnabled() {
        this.enableBridgingAssist = !this.isBridgingEnabled();
    }

}
