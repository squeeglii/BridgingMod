package me.cg360.mod.bridging.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.raytrace.PathTraversalHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class Render {

    public static void blocksInViewPath(PoseStack poseStack, VertexConsumer vertexConsumer, Camera camera) {
        LocalPlayer player = Minecraft.getInstance().player;

        if(player == null)
            return;

        List<BlockPos> path = PathTraversalHandler.getViewBlockPath(player);

        if(path.isEmpty())
            return;

        for(BlockPos pos: path)
            Render.cubeTrace(poseStack, vertexConsumer, camera, pos);
    }

    public static void cubeHighlight(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 0x260099FF);
    }

    public static void cubeTrace(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 0x16333333);
    }

    public static void cubeTermination(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 0x7FFF0000);
    }

    public static void cubeOutline(PoseStack poseStack, VertexConsumer consumer, Camera camera, BlockPos pos, int argbColor) {
        PoseStack.Pose pose = poseStack.last();
        Vec3 camPos = camera.getPosition();

        double x = pos.getX() - camPos.x();
        double y = pos.getY() - camPos.y();
        double z = pos.getZ() - camPos.z();

        Shapes.block().forAllEdges((startX, startY, startZ, endX, endY, endZ) -> {
            float dx = (float)(endX - startX);
            float dy = (float)(endY - startY);
            float dz = (float)(endZ - startZ);
            float length = Mth.sqrt(dx * dx + dy * dy + dz * dz);

            consumer.vertex(pose.pose(), (float)(startX + x), (float)(startY + y), (float)(startZ + z))
                    .color(argbColor)
                    .normal(pose.normal(), dx /= length, dy /= length, dz /= length)
                    .endVertex();

            consumer.vertex(pose.pose(), (float)(endX + x), (float)(endY + y), (float)(endZ + z))
                    .color(argbColor)
                    .normal(pose.normal(), dx, dy, dz)
                    .endVertex();
        });
    }



}
