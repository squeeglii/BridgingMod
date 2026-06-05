package me.cg360.mod.bridging.compat.impl;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.impl.environment.SableEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.VectorSupport;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class SableCompat {

    public static final Vector3f FORWARD_VEC = new Vector3f(0.0F, 0.0F, -1.0F); //Camera.FORWARDS

    public SableCompat() {
        SpecialHandlers.registerSpecialEnvironmentHandler(new SableEnvironmentHandler());
    }

    // todo: new thought - there's a good chance all the outline rendering just won't work as
    // Sable will have its own pipeline for rendering those far reaching regions. Try to just get the indicator working.

    public static Pose3d perspectiveToPose(Perspective perspective) {
        Quaternionf viewDeviation = new Quaternionf().rotationTo(FORWARD_VEC, perspective.getLookVector());

        return new Pose3d(
                new Vector3d(perspective.getPosition().toVector3f()),
                new Quaterniond(viewDeviation),
                new Vector3d(perspective.getPosition().toVector3f()),  // View rotated around position.
                new Vector3d(1, 1, 1)
        );
    }

    // It's easier in my head to just take the two points and rotate them,
    // then recalculate the look vector.
    public static Perspective transformOnPose(Perspective perspective, Pose3dc pose) {
        Vector3d pos = VectorSupport.toVector3d(perspective.getPosition());
        Vector3d lookPos = VectorSupport.toVector3d(perspective.getLookVector()).add(pos);

        pose.transformPositionInverse(pos); // mutate pos.
        pose.transformPositionInverse(lookPos).sub(pos); // Mutate lookpos using contraption space pos.

        BridgingMod.getLogger().info("Look Dir: {} (len: {})", lookPos, lookPos.length());

        return new Perspective(
                () -> VectorSupport.toVec3(pos),
                () -> VectorSupport.toVector3f(lookPos)
        );

    }

}
