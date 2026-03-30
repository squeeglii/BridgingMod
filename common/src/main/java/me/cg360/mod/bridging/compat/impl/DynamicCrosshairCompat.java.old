package me.cg360.mod.bridging.compat.impl;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.crosshair.Crosshair;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.world.phys.HitResult;

public class DynamicCrosshairCompat implements DynamicCrosshairApi {

	@Override
	public String getNamespace() {
		return "bridgingmod";
	}

	@Override
	public boolean forceInvalidate(CrosshairContext context) {
		return BridgingMod.getConfig().isBridgingEnabled()
				&& GameSupport.isHoldingPlaceable(context.getPlayer())
				&& context.getHitResult().getType() == HitResult.Type.MISS;
	}

	@Override
	public boolean forceCheck() {
		return true;
	}

	@Override
	public Crosshair computeFromItem(CrosshairContext context) {
		if (BridgingStateTracker.getLastTickTarget() != null) {
			return new Crosshair(InteractionType.PLACE_BLOCK);
		}

		return null;
	}
}
