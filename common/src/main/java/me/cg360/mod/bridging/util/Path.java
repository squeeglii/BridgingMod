package me.cg360.mod.bridging.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Path {

    public static final double NEAR_ZERO = 0.01D;
    public static final Vec3 CUBE_EXTENT = new Vec3(0.5f, 0.5f, 0.5f);

    public static List<BlockPos> calculateBresenhamVoxels(BlockPos startPos, BlockPos endPos) {
        List<BlockPos> points = new ArrayList<>();
        points.add(startPos);

        BlockPos delta = endPos.subtract(startPos);

        int dx = Math.abs(delta.getX());
        int dy = Math.abs(delta.getY());
        int dz = Math.abs(delta.getZ());

        int xStep = delta.getX() > 0 ? 1 : -1;
        int yStep = delta.getY() > 0 ? 1 : -1;
        int zStep = delta.getZ() > 0 ? 1 : -1;

        Vec3 workingVec = new Vec3(startPos.getX(), startPos.getY(), startPos.getZ());
        Vec3 targetVec = new Vec3(endPos.getX(), endPos.getY(), endPos.getZ());

        // X-axis
        if (dx >= dy && dx >= dz) {
            int point1 = 2 * dy - dx;
            int point2 = 2 * dz - dx;

            while (Math.abs(workingVec.x() - targetVec.x()) > NEAR_ZERO) {
                workingVec = workingVec.add(xStep, 0, 0);

                if (point1 >= 0) {
                    workingVec = workingVec.add(0, yStep, 0);
                    point1 -= 2 * dx;
                }

                if (point2 >= 0) {
                    workingVec = workingVec.add(0, 0, zStep);
                    point2 -= 2 * dx;
                }

                point1 += 2 * dy;
                point2 += 2 * dz;

                BlockPos newPoint = new BlockPos(workingVec);
                List<BlockPos> lostPoints = calculateMissedPoints(points, newPoint, startPos, endPos);

                points.addAll(lostPoints);
                points.add(newPoint);
            }

            return points;
        }

        // Y-axis
        if (dy >= dx && dy >= dz) {
            int point1 = 2 * dx - dy;
            int point2 = 2 * dz - dy;

            while (Math.abs(workingVec.y() - targetVec.y()) > NEAR_ZERO) {
                workingVec = workingVec.add(0, yStep, 0);

                if (point1 >= 0) {
                    workingVec = workingVec.add(xStep, 0, 0);
                    point1 -= 2 * dy;
                }

                if (point2 >= 0) {
                    workingVec = workingVec.add(0, 0, zStep);
                    point2 -= 2 * dy;
                }

                point1 += 2 * dx;
                point2 += 2 * dz;

                BlockPos newPoint = new BlockPos(workingVec);
                List<BlockPos> lostPoints = calculateMissedPoints(points, newPoint, startPos, endPos);

                points.addAll(lostPoints);
                points.add(newPoint);
            }

            return points;
        }

        // Z-axis
        int point1 = 2 * dy - dz;
        int point2 = 2 * dx - dz;

        while (Math.abs(workingVec.z() - targetVec.z()) > NEAR_ZERO) {
            workingVec = workingVec.add(0, 0, zStep);

            if (point1 >= 0) {
                workingVec = workingVec.add(0, yStep, 0);
                point1 -= 2 * dz;
            }

            if (point2 >= 0) {
                workingVec = workingVec.add(xStep, 0, 0);
                point2 -= 2 * dz;
            }

            point1 += 2 * dy;
            point2 += 2 * dx;

            BlockPos newPoint = new BlockPos(workingVec);
            List<BlockPos> lostPoints = calculateMissedPoints(points, newPoint, startPos, endPos);

            points.addAll(lostPoints);
            points.add(newPoint);
        }

        return points;
    }


    /**
     * Calculates any blocks missed by a pass of Bresenham's algorithm, under the condition
     * that two block positions provided are touching.
     */
    private static List<BlockPos> calculateMissedPoints(List<BlockPos> points, BlockPos newPoint, BlockPos lineStart, BlockPos lineEnd) {
        if(points.size() == 0) return List.of();

        BlockPos lastPoint = points.get(points.size() - 1);
        BlockPos pointDelta = newPoint.subtract(lastPoint);
        int diff = newPoint.distManhattan(lastPoint);

        // This method requires blocks to be touching.
        if(diff < 0 || diff > 3)
            throw new IllegalArgumentException("The last point and the new point share no common boundaries");

        // Blocks have a shared face or are even just the same,
        // thus the line stays contained within the two.
        if(diff == 1 || diff == 0) return List.of();

        List<BlockPos> reviewPositions = new LinkedList<>();


        // Blocks have a shared line, thus one of
        // the pointDelta's components must be zero.
        //
        //    o
        //       o  x     -- Trying to find the x's here.
        //       x  o
        if(diff == 2) {
            BlockPos[] checkDirections = new BlockPos[] {
                    new BlockPos(pointDelta.getX(), 0, 0),
                    new BlockPos(0, pointDelta.getY(), 0),
                    new BlockPos(0, 0, pointDelta.getZ())
            };

            for(BlockPos direction: checkDirections) {
                if(direction.equals(BlockPos.ZERO)) continue;

                BlockPos checkPos = lastPoint.offset(direction);
                reviewPositions.add(checkPos);
            }
        }

        // Blocks have a shared corner
        // bit harder to show as this needs thinking in 3d but it's
        // just a few more positions than diff == 2
        if(diff == 3) {
            BlockPos[] checkDirections = new BlockPos[] {
                    new BlockPos(pointDelta.getX(), 0, 0),
                    new BlockPos(0, pointDelta.getY(), 0),
                    new BlockPos(0, 0, pointDelta.getZ()),

                    new BlockPos(pointDelta.getX(), pointDelta.getY(), 0),
                    new BlockPos(pointDelta.getX(), 0, pointDelta.getZ()),
                    new BlockPos(0, pointDelta.getY(), pointDelta.getZ()),
            };

            for(BlockPos direction: checkDirections) {
                if(direction.equals(BlockPos.ZERO)) continue;

                BlockPos checkPos = lastPoint.offset(direction);
                reviewPositions.add(checkPos);
            }
        }

        // collision detection from -> https://3dkingdoms.com/weekly/weekly.php?a=21
        // retrofitted for the box checks


        return reviewPositions.stream()
                .filter(pos -> {
                    Vec3 boxSpaceTransform = Vec3.atLowerCornerOf(pos);
                    Vec3 lineStartD = Vec3.atLowerCornerOf(lineStart).subtract(boxSpaceTransform);
                    Vec3 lineEndD = Vec3.atLowerCornerOf(lineEnd).subtract(boxSpaceTransform);
                    Vec3 lineMid = lineStartD.add(lineEndD).scale(0.5f);
                    Vec3 line = lineStartD.subtract(lineMid);
                    Vec3 lineExt = new Vec3(Math.abs(line.x), Math.abs(line.y), Math.abs(line.z));

                    if (Math.abs( lineMid.x ) > CUBE_EXTENT.x + lineExt.x) return false;
                    if (Math.abs( lineMid.y ) > CUBE_EXTENT.y + lineExt.y) return false;
                    if (Math.abs( lineMid.z ) > CUBE_EXTENT.z + lineExt.z) return false;

                    // Crossproducts of line and each axis
                    if (Math.abs( lineMid.y * line.z - lineMid.z * line.y) > (CUBE_EXTENT.y * lineExt.z + CUBE_EXTENT.z * lineExt.y) ) return false;
                    if (Math.abs( lineMid.x * line.z - lineMid.z * line.x) > (CUBE_EXTENT.x * lineExt.z + CUBE_EXTENT.z * lineExt.x) ) return false;
                    if (Math.abs( lineMid.x * line.y - lineMid.y * line.x) > (CUBE_EXTENT.x * lineExt.y + CUBE_EXTENT.y * lineExt.x) ) return false;

                    // No separating axis, the line intersects

                    return true;
                })
                .toList();
    }

}
