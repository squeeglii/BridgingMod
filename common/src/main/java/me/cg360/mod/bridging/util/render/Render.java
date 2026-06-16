package me.cg360.mod.bridging.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.raytrace.*;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Render {

    public static void blocksInViewPath(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, BridgingPreContext initialContext) {
        LocalPlayer player = Minecraft.getInstance().player;

        if(player == null)
            return;

        // Sable support inplements perspective modifiers:
        BridgingPreContext context = PathTraversalHandler.adjustPathForSpecialHandlers(initialContext);

        List<BlockPos> path = PathTraversalHandler.getViewBlockPath(context);

        if(path.isEmpty())
            return;

        for(BlockPos pos: path)
            Render.cubeTrace(poseStack, submitNodeCollector, context.cameraPerspective(), pos);
    }

    public static void cubeHighlight(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Perspective view, BlockPos pos) {
        Render.cubeOutline(poseStack, submitNodeCollector, view, pos, 0x260099FF);
    }

    public static void cubeTrace(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Perspective view, BlockPos pos) {
        Render.cubeOutline(poseStack, submitNodeCollector, view, pos, 0x16333333);
    }

    public static void cubeTermination(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Perspective view, BlockPos pos) {
        Render.cubeOutline(poseStack, submitNodeCollector, view, pos, 0x7FFF0000);
    }

    public static void cubeOutline(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Perspective view, BlockPos pos, int argbColor) {
        Vec3 camPos = view.getPosition();

        double x = pos.getX() - camPos.x();
        double y = pos.getY() - camPos.y();
        double z = pos.getZ() - camPos.z();

        poseStack.pushPose();

        poseStack.translate(x, y, z);

        float lineWidth = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth;
        boolean afterTerrain = (argbColor & 0xFF000000) > 0; // todo: vanilla checks translucency in the blockoutlinerenderstate - I think this might be blockstate based.
        submitNodeCollector.submitShapeOutline(poseStack, Shapes.block(), RenderTypes.lines(), argbColor, lineWidth, true);

        poseStack.popPose();
    }


    public static void currentNonBridgingOutline(PoseStack poseStack, Perspective view, SubmitNodeCollector submitNodeCollector) {
        HitResult hit = Minecraft.getInstance().hitResult;

        // Skip non-placement hits.
        if(hit == null || hit.getType() != HitResult.Type.BLOCK)
            return;

        BlockHitResult blockHitResult = (BlockHitResult) hit;
        BlockPos hitBlock = blockHitResult.getBlockPos();
        Direction hitSide = blockHitResult.getDirection();

        BlockPos placeTarget = hitBlock.relative(hitSide);
        Player player = Minecraft.getInstance().player;

        if(player == null)
            return;

        if(!GameSupport.isHoldingPlaceable(player))
            return;

        // Avoid boxes beneath player feet - other entities should be fiiiine
        // Calculating collisions for every entity every tick just sounds messy.
        AABB placeDeadzone = new AABB(placeTarget);
        if (player.getBoundingBox().intersects(placeDeadzone))
            return;

        int outlineColour = BridgingMod.getConfig().getOutlineColour().getRGB();
        Render.cubeOutline(poseStack, submitNodeCollector, view, placeTarget, outlineColour);
    }

    public static void currentBridgingOutline(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, float partialTicks) {
        BridgingResult lastTarget = BridgingStateTracker.getLastTickTarget();

        if(lastTarget == null)
            return;

        if(lastTarget.context().flags().hasAll(Flags.SKIP_OUTLINE_RENDERING))
            return; // Usually if the rendering is in a weird state.

        int outlineColour = BridgingMod.getConfig().getOutlineColour().getRGB();

        AtomicBoolean hasRendered = new AtomicBoolean(false);
        CubeRenderTask renderTask = (poseStk, verts, perspective, pos, outlineCol) -> {
            Render.cubeOutline(poseStk, verts, perspective, pos, outlineCol);
            hasRendered.set(true);
        };

        SpecialHandlers.getSpecialEnvironmentHandlers()
                .forEach(handler -> handler.transformBridgingOutlineRendering(
                        lastTarget, renderTask, hasRendered.get(), partialTicks,
                        poseStack, submitNodeCollector, lastTarget.context().cameraPerspective(), lastTarget.blockPos(),
                        outlineColour));

        if(!hasRendered.get())
            renderTask.render(poseStack, submitNodeCollector, lastTarget.context().cameraPerspective(), lastTarget.blockPos(), outlineColour);
    }

}
