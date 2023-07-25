package me.cg360.mod.bridging.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

public class Render {

    public static void blocksInViewPath(PoseStack poseStack, VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Entity viewEntity = camera.getEntity();
        Vec3 viewDirection = viewEntity.getViewVector(partialTicks);
        Vec3 camPos = camera.getPosition();

        Vec3 forward = viewDirection.normalize();
        Vec3 unitAhead = camPos.add(forward.scale(2));
        Vec3 endUnit = camPos.add(forward.scale(5));

        BlockPos start = BlockPos.containing(unitAhead);
        BlockPos end = BlockPos.containing(endUnit);

        List<BlockPos> positions = Path.calculateBresenhamVoxels(start, end);

        if(positions.isEmpty()) return;

        //BlockPos lastPos = positions.remove(positions.size() - 1);

        for(BlockPos pos: positions)
            Render.cubeTrace(poseStack, vertexConsumer, camera, pos);

        //Render.cubeTermination(poseStack, vertexConsumer, camera, lastPos);
    }

    public static void cubeHighlight(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 0f, 0.6f, 1f, 0.15f);
    }

    public static void cubeTrace(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 0.2f, 0.2f, 0.2f, 0.1f);
    }

    public static void cubeTermination(PoseStack poseStack, VertexConsumer vertices, Camera camera, BlockPos pos) {
        Render.cubeOutline(poseStack, vertices, camera, pos, 1f, 0f, 0f, 0.5f);
    }

    public static void cubeOutline(PoseStack poseStack, VertexConsumer consumer, Camera camera, BlockPos pos, float red, float green, float blue, float alpha) {
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
                    .color(red, green, blue, alpha)
                    .normal(pose.normal(), dx /= length, dy /= length, dz /= length)
                    .endVertex();

            consumer.vertex(pose.pose(), (float)(endX + x), (float)(endY + y), (float)(endZ + z))
                    .color(red, green, blue, alpha)
                    .normal(pose.normal(), dx, dy, dz)
                    .endVertex();
        });
    }



}
