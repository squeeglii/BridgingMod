package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class PathTraceHandler {

    public static BlockPos lastTarget = null;

    private static final float MIN_DISTANCE = 1f;
    private static final double DIRECTION_SIMILARITY_THRESHOLD = 0.25d;

    public static Tuple<PlacementAlignment, BlockPos> getClosestAssistTarget(Player player) {
        ClientLevel level = Minecraft.getInstance().level;

        if(level == null)
            return null;

        List<BlockPos> path = PathTraceHandler.getViewBlockPath(player);
        Vec3 viewDirection = player.getViewVector(1f);

        List<Direction> validSides = PathTraceHandler.getValidAssistSides(viewDirection);

        PlacementAlignment alignmentIndicator = null;
        BlockPos validPos = null;

        for(BlockPos pos: path) {

            if(!level.getBlockState(pos).canBeReplaced())
                continue;

            Optional<PlacementAlignment> mostAligned = validSides.stream()
                    .map(dir -> PathTraceHandler.getAlignment(pos, dir))
                    .filter(Objects::nonNull)
                    .findFirst();

            if(mostAligned.isEmpty())
                continue;

            alignmentIndicator = mostAligned.get();
            validPos = pos;
            break;
        }

        lastTarget = validPos;

        return new Tuple<>(alignmentIndicator, validPos);
    }

    private static List<BlockPos> getViewBlockPath(Player player) {
        if(player == null)
            return new ArrayList<>();

        float eyeOffset = player.getEyeHeight(player.getPose());
        Vec3 viewOrigin = player.position().add(0, eyeOffset, 0);

        Vec3 viewDirection = player.getViewVector(1f);
        Vec3 nearVec = viewDirection.scale(MIN_DISTANCE);
        Vec3 farVec = viewDirection.scale(GameSupport.getReach());

        BlockPos startPos = BlockPos.containing(viewOrigin.add(nearVec));
        BlockPos endPos = BlockPos.containing(viewOrigin.add(farVec));

        return Path.calcBresenhamSquares(startPos, endPos);
    }

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

    private static PlacementAlignment getAlignment(BlockPos block, Direction checkSide) {
        ClientLevel level = Minecraft.getInstance().level;

        if(level == null)
            return null;

        BlockPos checkBlockPos = block.offset(checkSide.getNormal());

        if(level.isEmptyBlock(checkBlockPos))
            return null;

        if(checkSide == Direction.DOWN) return PlacementAlignment.UP;
        if(checkSide == Direction.UP) return PlacementAlignment.DOWN;

        return PlacementAlignment.HORIZONTAL;
    }

}
