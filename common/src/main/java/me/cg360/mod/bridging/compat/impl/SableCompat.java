package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.impl.environment.SableEnvironmentHandler;

public class SableCompat {

    public SableCompat() {
        SpecialHandlers.registerSpecialEnvironmentHandler(new SableEnvironmentHandler());
    }

}
