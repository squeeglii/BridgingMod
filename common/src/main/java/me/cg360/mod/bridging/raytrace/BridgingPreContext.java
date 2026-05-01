package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record BridgingPreContext(Level level, Perspective cameraPerspective, Perspective playerPerspective, Player player, Flags flags) {

}
