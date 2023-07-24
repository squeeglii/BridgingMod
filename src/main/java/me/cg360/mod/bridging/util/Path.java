package me.cg360.mod.bridging.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Path {

    public static final double NEAR_ZERO = 0.01D;

    public static List<BlockPos> calcBresenhamSquares(BlockPos startPos, BlockPos endPos) {
        List<BlockPos> points = new ArrayList<>();
        points.add(startPos);

        BlockPos delta = endPos.subtract(startPos);

        int dx = Math.abs(delta.getX());
        int dy = Math.abs(delta.getY());
        int dz = Math.abs(delta.getZ());

        int xs = delta.getX() > 0 ? 1 : -1;
        int ys = delta.getY() > 0 ? 1 : -1;
        int zs = delta.getZ() > 0 ? 1 : -1;

        Vec3 workingVec = new Vec3(startPos.getX(), startPos.getY(), startPos.getZ());
        Vec3 targetVec = new Vec3(endPos.getX(), endPos.getY(), endPos.getZ());

        // X-axis
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;

            while (Math.abs(workingVec.x() - targetVec.x()) > NEAR_ZERO) {
                workingVec = workingVec.add(xs, 0, 0);

                if (p1 >= 0) {
                    workingVec = workingVec.add(0, ys, 0);
                    p1 -= 2 * dx;
                }

                if (p2 >= 0) {
                    workingVec = workingVec.add(0, 0, zs);
                    p2 -= 2 * dx;
                }

                p1 += 2 * dy;
                p2 += 2 * dz;

                points.add(BlockPos.containing(workingVec));
            }

            return points;
        }

        // Y-axis
        if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;

            while (Math.abs(workingVec.y() - targetVec.y()) > NEAR_ZERO) {
                workingVec = workingVec.add(0, ys, 0);

                if (p1 >= 0) {
                    workingVec = workingVec.add(xs, 0, 0);
                    p1 -= 2 * dy;
                }

                if (p2 >= 0) {
                    workingVec = workingVec.add(0, 0, zs);
                    p2 -= 2 * dy;
                }

                p1 += 2 * dx;
                p2 += 2 * dz;
                points.add(BlockPos.containing(workingVec));
            }

            return points;
        }

        // Z-axis
        int p1 = 2 * dy - dz;
        int p2 = 2 * dx - dz;

        while (Math.abs(workingVec.z() - targetVec.z()) > NEAR_ZERO) {
            workingVec = workingVec.add(0, 0, zs);

            if (p1 >= 0) {
                workingVec = workingVec.add(0, ys, 0);
                p1 -= 2 * dz;
            }

            if (p2 >= 0) {
                workingVec = workingVec.add(xs, 0, 0);
                p2 -= 2 * dz;
            }

            p1 += 2 * dy;
            p2 += 2 * dx;
            points.add(BlockPos.containing(workingVec));
        }

        return points;
    }

}
