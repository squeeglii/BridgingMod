package me.cg360.mod.bridging.raytrace;

import net.minecraft.core.Direction;

/**
 * Used to determine the indicator that should be
 * used when bridging assist is available.
 */
public enum PlacementAlignment {

    UP(64),
    DOWN(0),
    HORIZONTAL(32);


    private final int textureOffset;

    PlacementAlignment(int textureOffset) {
        this.textureOffset = textureOffset;
    }

    public int getTextureOffset() {
        return this.textureOffset;
    }

    public static PlacementAlignment from(Direction direction) {
        if(direction == null) return null;

        return switch (direction) {
            case UP -> DOWN;
            case DOWN -> UP;
            default -> HORIZONTAL;
        };
    }

}
