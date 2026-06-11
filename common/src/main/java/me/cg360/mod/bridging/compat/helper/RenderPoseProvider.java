package me.cg360.mod.bridging.compat.helper;

import dev.ryanhcode.sable.companion.math.Pose3dc;

import java.util.Optional;

@FunctionalInterface
public interface RenderPoseProvider {

    Optional<Pose3dc> getRenderPose(float partialTicks);

}
