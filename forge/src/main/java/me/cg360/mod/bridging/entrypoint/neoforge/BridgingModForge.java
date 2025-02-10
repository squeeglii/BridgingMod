package me.cg360.mod.bridging.entrypoint.neoforge;

import me.cg360.mod.bridging.BridgingMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(value = BridgingMod.MOD_ID)
public class BridgingModForge {

    public BridgingModForge() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BridgingModClientForge::new);
    }

}
