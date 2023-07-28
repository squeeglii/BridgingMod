package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class PathTraversalHandler {

    private static final float MIN_DISTANCE = 1f;
    private static final double DIRECTION_SIMILARITY_THRESHOLD = 0.1d;

    /**
     * @param player the player whose view line should be used.
     * @return the closest block position in view that supports bridge assist.
     */
    public static Tuple<BlockPos, Direction> getClosestAssistTarget(Player player) {
        ClientLevel level = Minecraft.getInstance().level;

        if(level == null)
            return null;

        List<BlockPos> path = PathTraversalHandler.getViewBlockPath(player);

        Vec3 viewDirection = player.getViewVector(1f);
        List<Direction> validSides = PathTraversalHandler.getValidAssistSides(viewDirection);

        Direction validDirection = null;
        BlockPos validPos = null;

        // Check each position in-order between the camera and the end of reach
        for(BlockPos pos: path) {

            // Invalidate any position that can't have blocks placed there normally.
            if(!level.getBlockState(pos).getMaterial().isReplaceable())
                continue;

            Vec3 collideMin = Vec3.atLowerCornerOf(pos);
            Vec3 collideMax = Vec3.atLowerCornerOf(pos).add(1, 1, 1);

            // Invalidate any position that is within the player's bounding box.
            if(player.getBoundingBox().intersects(collideMin, collideMax))
                continue;

            // Test all the sides a given position could be built off and accept the
            // first valid one. Validity includes them being placeable against, as well
            // as facing a similar direction to the camera.
            Optional<Direction> firstValidDirection = validSides.stream()
                    .filter(dir -> PathTraversalHandler.isValidDirection(pos, dir))
                    .findFirst();

            if(firstValidDirection.isEmpty())
                continue;

            validDirection = firstValidDirection.get();
            validPos = pos;
            break;
        }

        if(validDirection == null || validPos == null)
            return null;

        return new Tuple<>(validPos, validDirection);
    }

    /**
     * Generates a list of blocks which follow the reach line of a given
     * player from a certain distance.
     */
    public static List<BlockPos> getViewBlockPath(Player player) {
        if(player == null)
            return new ArrayList<>();

        float eyeOffset = player.getEyeHeight(player.getPose());
        Vec3 viewOrigin = player.position().add(0, eyeOffset, 0);

        Vec3 viewDirection = player.getViewVector(1f);
        Vec3 nearVec = viewDirection.scale(MIN_DISTANCE);
        Vec3 farVec = viewDirection.scale(GameSupport.getReach());

        BlockPos startPos = new BlockPos(viewOrigin.add(nearVec));
        BlockPos endPos = new BlockPos(viewOrigin.add(farVec));

        return Path.calculateBresenhamVoxels(startPos, endPos);
    }

    /**
     * Gathers a list of sides that roughly align with the opposite direction
     * that the view is facing, then returning their opposites indicating at
     * what offset these sides can be found compared to a blockpos
     */
    private static List<Direction> getValidAssistSides(Vec3 viewDirection) {
        LinkedList<Direction> validSides = new LinkedList<>();

        for(Direction direction: Direction.values()) {
            Vec3 directionNormal = Vec3.atLowerCornerOf(direction.getNormal());

            double similarity = viewDirection.dot(directionNormal);

            if(similarity < DIRECTION_SIMILARITY_THRESHOLD)
                continue;

            validSides.add(direction.getOpposite());
        }

        return validSides;
    }

    /**
     * Determines if a block can be placed at the position "block",
     * if building off of a surface in a given direction when in relation to the position
     * surface|  <<< checkSide <<< |block
     */
    private static boolean isValidDirection(BlockPos block, Direction checkSide) {
        ClientLevel level = Minecraft.getInstance().level;

        if(level == null)
            return false;

        BlockPos checkBlockPos = block.offset(checkSide.getNormal());

        if(level.isEmptyBlock(checkBlockPos)) return false;
        if(level.getBlockState(checkBlockPos).getBlock() instanceof LiquidBlock) return false;

        return !level.isEmptyBlock(checkBlockPos);
    }

}
