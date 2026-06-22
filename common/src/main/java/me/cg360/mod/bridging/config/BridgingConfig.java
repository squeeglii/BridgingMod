package me.cg360.mod.bridging.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.helper.*;
import me.cg360.mod.bridging.config.selector.BridgingAdjacency;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import me.cg360.mod.bridging.config.selector.PlacementAxisMode;
import me.cg360.mod.bridging.config.selector.PlacementAxisModeOverride;

import java.awt.*;

public class BridgingConfig extends DefaultValueTracker {

    public static ConfigClassHandler<BridgingConfig> HANDLER = ConfigClassHandler.createBuilder(BridgingConfig.class)
            .id(BridgingMod.id("main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(BridgingMod.getDefaultConfigPath().resolve(BridgingMod.MOD_ID + ".json"))
                    .setJson5(false)
                    .build())
            .build();

    public BridgingConfig() {
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }


    @SerialEntry @HideInConfigUI
    private int version = 6;

    @SerialEntry @HideInConfigUI // gson. why is there json5 support without comments.
    private String __comment_enableBridgingAssist = "a.k.a. 'Reacharound Placement' in keybinds. The Quark thing.";
    @Category("feature") @SerialEntry
    @IncludeAnimatedImage("textures/gui/config/bridging.webp")
    @IncludeExtraDescription(extraParagraphs = 2)
    private boolean enableBridgingAssist = true;
    @Category("feature") @SerialEntry
    @ContinuousRange(min = 0.0f, max = 100.0f, sliderStep = 0.5f, formatTranslationKey = "config.bridgingmod.value.percentage")
    private float minBridgeDistance = 20f;
    @HideInConfigUI @Category("feature") @SerialEntry
    @ContinuousRange(min = 0.0f, max = 10.0f, sliderStep = 0.1f, formatTranslationKey = "config.bridgingmod.value.distance")   // Should this just be a box?
    private float minBridgeDistanceHorizontal = 1.0f; // TODO: FIX!
    @HideInConfigUI  @Category("feature") @SerialEntry
    @ContinuousRange(min = 0.0f, max = 10.0f, sliderStep = 0.1f, formatTranslationKey = "config.bridgingmod.value.distance")   // Should this just be a box?
    private float minBridgeDistanceVertical = 0.1f;  // TODO: FIX!
    @Category("feature") @SerialEntry
    @IncludeExtraDescription
    private boolean onlyBridgeWhenCrouched = false;
    @Category("feature") @SerialEntry
    private PlacementAxisMode supportedBridgeAxes = PlacementAxisMode.BOTH;
    @Category("feature") @SerialEntry
    private PlacementAxisModeOverride supportedBridgeAxesWhenCrouched = PlacementAxisModeOverride.FALLBACK;
    @Category("feature") @SerialEntry
    @IncludeExtraDescription(extraParagraphs = 2)
    @DiscreteRange(min = 0, max = 20, formatTranslationKey = "config.bridgingmod.value.ticks")
    private int delayPostBridging = 4; // 4 is vanilla - 3 allows for better forward bridging.


    @Category("vfx") @SerialEntry
    @IncludeImage("textures/gui/config/show_crosshair.png")
    private boolean showCrosshair = true;
    @Category("vfx") @SerialEntry
    @IncludeImage("textures/gui/config/bridging_outline.png")
    private boolean showOutline = true;
    @Category("vfx") @SerialEntry
    @IncludeImage("textures/gui/config/non_bridging_outline.png")
    private boolean showOutlineEvenWhenNotBridging = false;
    @Category("vfx") @SerialEntry
    @IncludeExtraDescription
    private boolean nonBridgeRespectsCrouchRules = true;
    @Category("vfx") @SerialEntry
    @IncludeImage("textures/gui/config/outline_colour.png")
    @IncludeExtraDescription
    private Color outlineColour = new Color(0, 0, 0, 0.4f);


    /* = Fixes = */
    /* Fixes are simple toggles that are a bit too nitpicky for the features tab.*/
    @Category("fixes") @SerialEntry
    private boolean skipTorchBridging = true;
    @Category("fixes") @SerialEntry
    @IncludeExtraDescription(extraParagraphs = 3)
    @IncludeAnimatedImage("textures/gui/config/bridging_slabs_horizontal.webp")
    private boolean enableSlabAssist = true;
    @Category("fixes") @SerialEntry
    private boolean enableNonSolidReplace = true;
    @Category("fixes") @SerialEntry
    @ContinuousRange(min = 0.0f, max = 1.0f, sliderStep = 0.05f)
    @IncludeExtraDescription(extraParagraphs = 4)
    private float bridgingSnapStrength = 1.0f;
    @Category("fixes") @SerialEntry
    @IncludeExtraDescription(extraParagraphs = 4)
    private BridgingAdjacency bridgingAdjacency = BridgingAdjacency.CORNERS;
    @Category("fixes") @SerialEntry
    @IncludeExtraDescription
    private SourcePerspective perspectiveLock = SourcePerspective.LET_BRIDGING_MOD_DECIDE;


    @Category("debug") @SerialEntry
    private boolean showDebugHighlight = true;
    @Category("debug") @SerialEntry
    private boolean showNonBridgingDebugHighlight = false;
    @Category("debug") @SerialEntry
    private boolean showDebugTrace = false;




    public boolean isBridgingEnabled() {
        return this.enableBridgingAssist;
    }

    public float getMinimumBridgeDistance() {
        return this.minBridgeDistance;
    }

    public float getMinimumBridgeDistanceHorizontal() {
        return this.minBridgeDistanceHorizontal;
    }

    public float getMinimumBridgeDistanceVertical() {
        return this.minBridgeDistanceVertical;
    }

    public float getBridgingSnapStrength() {
        return this.bridgingSnapStrength;
    }

    public BridgingAdjacency getBridgingAdjacency() {
        return this.bridgingAdjacency;
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

    public SourcePerspective getPerspectiveLock() {
        return this.perspectiveLock;
    }

    public void toggleBridgingEnabled() {
        this.enableBridgingAssist = !this.isBridgingEnabled();
        BridgingConfig.HANDLER.save();
    }

    public void upgrade() {
        this.version = 5;
    }


}
