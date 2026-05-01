package me.cg360.mod.bridging.raytrace;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record BridgingPreContext(Level level, Perspective perspective, Player player) {

}
