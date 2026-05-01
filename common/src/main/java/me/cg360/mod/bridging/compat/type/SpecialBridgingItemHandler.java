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
public interface SpecialBridgingItemHandler {

    /**
     * @return If stack is valid for placement without any world context.
     *         this can be used to make non-blocks placeable.
     *         If you want to run default checks, use GameSupport#passesDefaultPlacementCheck(...)
     */
    boolean canBePlaced(ItemStack stack);

    /**
     * @return More detailed check for placement validity.
     *         Determines if the currently picked placement position is valid.
     */
    default boolean canBePlacedInWorld(ItemStack stack, Player player, Level level, BlockPos pos, Direction direction) {
        return player.mayUseItemAt(pos, direction, stack);
    }

    /**
     * @return The result of the placement interaction.
     *         If null, BridgingMod assumes it needs to place the block itself.
     *         If any other value, BridgingMod skips placing the block and assumes
     *         this method has handled placing it.
     */
    default InteractionResult place(/* todo: context */) {
        return null; // default: pass placing to bridging mod.
    }

    /**
     * @return A BlockHitResult for the angle at which a block should be virtually placed from
     *         i.e, mimicing bridgingmod$getFinalPlaceAssistTarget(...)
     *         If null is returned, BridgingMod calculates the target using the default method.
     *         If you want to use the default method with some extra checks, consider calling
     *         Bridge#getDefaultPlaceAssistTarget(...)
     */
    default BlockHitResult generatePlacementTarget(ItemStack stack, Player player, Level level, Direction direction, BlockPos pos) {
        return null;
    }
}
