package me.cg360.mod.bridging.compat.type;

import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

/**
 * Extend this and register it in SpecialHandlers!
 */
public interface SpecialBridgingEnvironmentHandler {

    /**
     * @return Modify the properties used to calculate the bridging scan path
     */
    default Optional<BridgingPreContext> generatePlacementContextOverride(BridgingPreContext initialContext) {
        return Optional.empty();
    }

}
