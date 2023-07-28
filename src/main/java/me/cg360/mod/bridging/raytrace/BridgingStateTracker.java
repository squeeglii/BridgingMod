package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public class BridgingStateTracker {

    private static Tuple<BlockPos, Direction> lastTickTarget = null;


    /**
     * Generates a bridge assist target for a given player, under the condition
     * that they can't already place a block under vanilla conditions.
     */
    public static Tuple<BlockPos, Direction> getBridgeAssistTargetFor(Player player) {
        if(player == null)
            return null;

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


    public static Tuple<BlockPos, Direction> getLastTickTarget() {
        return BridgingStateTracker.lastTickTarget;
    }

}
