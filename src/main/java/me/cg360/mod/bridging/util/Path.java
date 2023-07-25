package me.cg360.mod.bridging.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Path {

    public static final double NEAR_ZERO = 0.01D;

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

                points.add(BlockPos.containing(workingVec));
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
                points.add(BlockPos.containing(workingVec));
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
            points.add(BlockPos.containing(workingVec));
        }

        return points;
    }

}
