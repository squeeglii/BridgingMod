package me.cg360.mod.bridging.compat.type;

import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.BridgingResult;
import me.cg360.mod.bridging.util.render.CubeRenderTask;

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

    default void transformOutlineRendering(BridgingResult result, CubeRenderTask task, boolean hasAlreadyRendered) { }

}
