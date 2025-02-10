package me.cg360.mod.bridging.building;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.BridgingCrosshairTweaks;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class Bridge {

    /**
     * This method used the mod's original way of creating a BlockHitResult to
     * simulate placing a block from the valid angle.
     *
     * This can be overriden for compatibility with SpecialBridgingHandlers. See:
     * MinecraftClientMixin#bridgingmod$getFinalPlaceAssistTarget(...)
     */
    public static BlockHitResult getDefaultPlaceAssistTarget(ItemStack heldItem, Level level, Direction dir, BlockPos pos) {
        if(BridgingMod.getConfig().isSlabAssistEnabled() && heldItem.getItem() instanceof BlockItem heldBlockItem) {
            Block placementBlock = heldBlockItem.getBlock();
            boolean isSlabAssistTarget = SpecialHandlers.slabAssistFilters
                    .stream()
                    .anyMatch(f -> f.apply(placementBlock));

            if(isSlabAssistTarget) {
                BlockHitResult override = switch (dir.getAxis()) {
                    case X, Z -> handleHorizontalSlabAssist(pos);
                    case Y -> handleVerticalSlabAssist(heldItem, level, dir, pos);
                };

                if (override != null) return override;
            }
        }

        Vec3 placerOrigin = Vec3.atCenterOf(pos);
        return new BlockHitResult(placerOrigin, dir, pos, true);
    }

    /** Generate a fake valid placement raycast for a horizontal slab. */
    private static BlockHitResult handleHorizontalSlabAssist(BlockPos pos) {
        // Slab assist should also help with trapdoors.
        // I would get stairs to work too but that either requires major jank
        // or server-side mods.

        boolean shouldTargetLowerHalf = BridgingStateTracker.lastKnownYFrac > GameSupport.TRAPDOOR_HEIGHT - Path.NEAR_ZERO
                && BridgingStateTracker.lastKnownYFrac < GameSupport.SLAB_HEIGHT + Path.NEAR_ZERO;

        Vec3 placerOrigin = shouldTargetLowerHalf
                ? Vec3.atBottomCenterOf(pos).add(0, 0.1d, 0)
                : Vec3.atBottomCenterOf(pos).add(0, 0.9d, 0);

        // A bit of jank to get trapdoors working. context.replacingClickedOnBlock() seems to always == true
        // with my current impl so let's ignore that. I don't think this has major side-effects?
        Direction placeDir = shouldTargetLowerHalf
                ? Direction.UP
                : Direction.DOWN;

        // When jank is fixed, replace 'placeDir' with 'dir'
        return new BlockHitResult(placerOrigin, placeDir, pos, false);
    }

    /** Generate a fake valid placement raycast for a vertical slab. Handles combining slab blocks too. */
    private static BlockHitResult handleVerticalSlabAssist(ItemStack heldItem, Level level, Direction dir, BlockPos pos) {
        if(level == null) return null;
        if(!(heldItem.getItem() instanceof BlockItem blockItem)) return null;
        if(!(blockItem.getBlock() instanceof SlabBlock)) return null;

        // Get the block to place "upgrade" the slab from
        BlockPos buildingOffPos = pos.offset(dir.getNormal().multiply(-1));
        BlockState localState = level.getBlockState(buildingOffPos);

        if(!(localState.getBlock() instanceof SlabBlock)) return null;
        SlabType slabType = localState.getValue(SlabBlock.TYPE);

        // Check if the placement will even be accepted by the game.
        if(slabType == SlabType.DOUBLE) return null;
        if(slabType == SlabType.TOP && dir != Direction.DOWN) return null;
        if(slabType == SlabType.BOTTOM && dir != Direction.UP) return null;

        Vec3 placerOrigin = Vec3.atCenterOf(pos);
        return new BlockHitResult(placerOrigin, dir, buildingOffPos, false);
    }
}
