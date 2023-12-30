package me.cg360.mod.bridging.util;

import net.minecraft.core.Direction;

public enum PlacementAxisMode {
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
}
