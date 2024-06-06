package me.cg360.mod.bridging.entrypoint.forge;

import me.cg360.mod.bridging.BridgingMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(BridgingMod.MOD_ID)
public class BridgingModForge {

    private static final String DYNAMIC_CROSSHAIR_MOD = "dynamiccrosshair";

    public BridgingModForge() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BridgingModClientForge::new);
    }

}
