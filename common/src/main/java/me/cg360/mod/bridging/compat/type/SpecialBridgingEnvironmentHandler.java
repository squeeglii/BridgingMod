package me.cg360.mod.bridging.compat.type;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.BridgingResult;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.render.CubeRenderTask;
import net.minecraft.core.BlockPos;

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

    default void transformBridgingOutlineRendering(BridgingResult result, CubeRenderTask task, boolean hasAlreadyRendered, float partialTicks, PoseStack poseStack, VertexConsumer vertices, Perspective perspective, BlockPos pos, int outlineColour) { }

    default void transformNonBridgingOutlineRendering(CubeRenderTask task, boolean hasAlreadyRendered, float partialTicks, PoseStack poseStack, VertexConsumer vertices, Perspective perspective, BlockPos pos, int outlineColour) { }

    default boolean forceHideCrosshair(BridgingResult result) { return false; }

    default Optional<Integer> modifyCrosshairHeight(int yShift, boolean alreadyModified) {
        return Optional.empty();
    }

}
