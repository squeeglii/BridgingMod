package me.cg360.mod.bridging;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "bridgingmod")
public class BridgingConfig implements ConfigData {

    private boolean enableBridgingAssist = true;

    private boolean showCrosshair = true;

    private boolean showDebugHighlight = true;
    private boolean showDebugTrace = false;


    public boolean isBridgingEnabled() {
        return this.enableBridgingAssist;
    }

    public boolean shouldShowDebugHighlight() {
        return this.showDebugHighlight;
    }

    public boolean shouldShowDebugTrace() {
        return this.showDebugTrace;
    }

    public boolean shouldShowCrosshair() {
        return this.showCrosshair;
    }
}
