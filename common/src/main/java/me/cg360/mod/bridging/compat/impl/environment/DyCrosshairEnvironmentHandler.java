package me.cg360.mod.bridging.compat.impl.environment;

import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingResult;

public class DyCrosshairEnvironmentHandler implements SpecialBridgingEnvironmentHandler {

    @Override
    public boolean forceHideCrosshair(BridgingResult result) {
        return true;
    }


}
