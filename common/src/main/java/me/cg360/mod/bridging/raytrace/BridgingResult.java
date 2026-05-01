package me.cg360.mod.bridging.raytrace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record BridgingResult(BlockPos blockPos, Direction direction, BridgingPreContext context) {
}
