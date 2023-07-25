package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public class ReacharoundTracker {

    public static Tuple<BlockPos, Direction> lastTickTarget = null;


    public static Tuple<BlockPos, Direction> getPlayerReacharoundTarget(Player player) {
        if(player == null)
            return null;

        HitResult hit = Minecraft.getInstance().hitResult;

        // If there's a valid block to build on in view & range, do not calculate reach-around.
        if(hit != null && hit.getType() != HitResult.Type.MISS)
            return null;

        // Check if either stack can be placed, else don't show the guide.
        if(!GameSupport.isHoldingPlaceable(player))
            return null;

        return PathTraceHandler.getClosestAssistTarget(player);
    }

}
