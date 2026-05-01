package me.cg360.mod.bridging.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.raytrace.Perspective;
import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface CubeRenderTask {

    void render(PoseStack poseStack, VertexConsumer consumer, Perspective view, BlockPos pos, int argbColor);

}
