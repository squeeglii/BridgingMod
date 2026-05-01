package me.cg360.mod.bridging.compat.impl;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.helper.RenderPoseProvider;
import me.cg360.mod.bridging.compat.impl.environment.SableEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.VectorSupport;
import me.cg360.mod.bridging.util.flags.Flag;
import org.joml.*;

import java.util.Optional;

public class SableCompat {

    public static final RenderPoseProvider NULL_RENDER_POSE = partialTicks -> Optional.empty();

    public static final Flag IN_SUB_LEVEL = new Flag("WITHIN_SUB_LEVEL");


    private static SableCompat instance = null;
    private RenderPoseProvider lastContraptionPose = NULL_RENDER_POSE;

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

    public void setLastContraptionPose(RenderPoseProvider poseProvider) {
        this.lastContraptionPose = poseProvider;
    }

    public void nullLastContraptionPose() {
        this.lastContraptionPose = NULL_RENDER_POSE;
    }

    public Optional<Pose3dc> getLastContraptionPose(float partialTicks) {
        return this.lastContraptionPose.getRenderPose(partialTicks);
    }

    public static SableCompat get() {
        return instance;
    }

    public static Optional<SableCompat> getOpt() {
        return Optional.ofNullable(instance);
    }

}
