package me.cg360.mod.bridging.compat.neoforge;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.world.phys.HitResult;

public class DynamicCrosshairCompat implements DynamicCrosshairApi {

	@Override
	public String getNamespace() {
		return "bridgingmod";
	}

	@Override
	public boolean forceInvalidate(CrosshairContext context) {
		return BridgingMod.getConfig().isBridgingEnabled()
				&& GameSupport.isHoldingPlaceable(context.player)
				&& context.hitResult.getType() == HitResult.Type.MISS;
	}

	@Override
	public boolean forceCheck() {
		return true;
	}

	@Override
	public Crosshair computeFromItem(CrosshairContext context) {
		if (BridgingStateTracker.getLastTickTarget() != null) {
			return Crosshair.HOLDING_BLOCK;
		}
		return null;
	}

}
