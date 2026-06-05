package me.cg360.mod.bridging.compat.impl;

import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.impl.environment.SableEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.VectorSupport;
import me.cg360.mod.bridging.util.flags.Flag;
import org.joml.*;

import java.util.Optional;

public class SableCompat {

    public static final Vector3f FORWARD_VEC = new Vector3f(0.0F, 0.0F, -1.0F); //Camera.FORWARDS

    public static final Flag IN_SUB_LEVEL = new Flag("WITHIN_SUB_LEVEL");


    private static SableCompat instance = null;
    private Pose3dc lastContraptionPose = null;

    public SableCompat() {
        SpecialHandlers.registerSpecialEnvironmentHandler(new SableEnvironmentHandler());
    }

    public void setAsInstance() {
        instance = this;
    }

    // It's easier in my head to just take the two points and rotate them,
    // then recalculate the look vector.
    public static Perspective transformOnPose(Perspective perspective, Pose3dc pose) {
        Vector3d pos = VectorSupport.toVector3d(perspective.getPosition());
        Vector3d lookPos = VectorSupport.toVector3d(perspective.getLookVector()).add(pos);

        pose.transformPositionInverse(pos); // mutate pos.
        pose.transformPositionInverse(lookPos).sub(pos); // Mutate lookpos using contraption space pos.

        return new Perspective(
                () -> VectorSupport.toVec3(pos),
                () -> VectorSupport.toVector3f(lookPos)
        );

    }

    public void setLastContraptionPose(Pose3dc pose) {
        this.lastContraptionPose = pose;
    }

    public void nullLastContraptionPose() {
        this.lastContraptionPose = null;
    }

    public Pose3dc getLastContraptionPose() {
        return this.lastContraptionPose;
    }

    public static SableCompat get() {
        return instance;
    }

    public static Optional<SableCompat> getOpt() {
        return Optional.ofNullable(instance);
    }

}
