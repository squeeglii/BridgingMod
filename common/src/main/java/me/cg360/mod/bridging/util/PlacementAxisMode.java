package me.cg360.mod.bridging.util;


import me.cg360.mod.bridging.config.helper.Translatable;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public enum PlacementAxisMode implements Translatable {

    HORIZONTAL(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST),
    VERTICAL(Direction.UP, Direction.DOWN),
    BOTH(Direction.values());

    private final Direction[] directions;

    PlacementAxisMode(Direction... supportedDirections) {
        this.directions = supportedDirections;
    }

    public boolean isDirectionEnabled(Direction direction) {
        for(Direction dir: this.directions) {
            if(dir == direction)
                return true;
        }

        return false;
    }

    @Override
    @NotNull
    public String getTranslationKey() {
        return "enum.bridgingmod.placement_axis.%s".formatted(this.name().toLowerCase());
    }
}
