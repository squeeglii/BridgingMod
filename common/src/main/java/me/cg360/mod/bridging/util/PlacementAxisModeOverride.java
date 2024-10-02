package me.cg360.mod.bridging.util;

import me.cg360.mod.bridging.config.helper.Translatable;
import org.jetbrains.annotations.NotNull;

public enum PlacementAxisModeOverride implements Translatable {
    HORIZONTAL(PlacementAxisMode.HORIZONTAL),
    VERTICAL(PlacementAxisMode.VERTICAL),
    BOTH(PlacementAxisMode.BOTH),
    FALLBACK(null);

    private final PlacementAxisMode base;

    PlacementAxisModeOverride(PlacementAxisMode base) {
        this.base = base;
    }

    public PlacementAxisMode getPlacementAxisMode(PlacementAxisMode fallback) {
        return this.base == null
                ? fallback
                : this.base;
    }

    @Override
    @NotNull
    public String getTranslationKey() {
        return "enum.bridgingmod.placement_axis.%s".formatted(this.name().toLowerCase());
    }
}
