package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.BridgingCrosshairTweaks;
import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BridgingStateTracker {

    private static BridgingResult lastTickTarget = null;

    public static double lastKnownYFrac = 0; // used for slab assist.

    /**
     * Generates a bridge assist target for a given player, under the condition
     * that they can't already place a block under vanilla conditions.
     */
    public static BridgingResult getBridgeAssistTargetFor(Player player) {
        if(player == null)
            return null;

        // this miss check kinda falls apart when freelook like mods are installed.
        // todo: make a hitresult cast from the camera, through the player?
        HitResult hit = Minecraft.getInstance().hitResult;

        // If there's a valid block to build on in view & range, do not calculate reach-around.
        if(hit != null && hit.getType() != HitResult.Type.MISS)
            return null;

        // Check if either stack can be placed, else don't show the guide.
        if(!GameSupport.isHoldingPlaceable(player))
            return null;

        return PathTraversalHandler.getClosestAssistTarget(player);
    }

    public static void tick(LocalPlayer player) {
        if(!BridgingMod.getConfig().isBridgingEnabled()) {
            lastTickTarget = null;
            return;
        }

        lastTickTarget = BridgingStateTracker.getBridgeAssistTargetFor(player);
    }

    public static BridgingResult getLastTickTarget() {
        return BridgingStateTracker.lastTickTarget;
    }

}
