package me.cg360.mod.bridging.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class VectorSupport {

    public static Vec3 toVec3(Vector3dc vec) {
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

    public static Vector3d toVector3d(Vec3 vec) {
        return new Vector3d(vec.x, vec.y, vec.z);
    }

    public static Vector3d toVector3d(Vector3fc vec) {
        return new Vector3d(vec);
    }

    public static Vector3f toVector3f(Vector3dc vec) {
        return new Vector3f(
                (float) vec.x(),
                (float) vec.y(),
                (float) vec.z()
        );
    }

}
