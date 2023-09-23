package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class DebugLevelRendererMixin {

    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At("RETURN"))
    public void renderTracedViewPath(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        if(!this.minecraft.getDebugOverlay().showDebugScreen()) return;

        VertexConsumer vertices = this.renderBuffers.bufferSource().getBuffer(RenderType.lines());

        if(BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, camera);

        Tuple<BlockPos, Direction> lastTarget = BridgingStateTracker.getLastTickTarget();

        if(lastTarget != null && BridgingMod.getConfig().shouldShowDebugHighlight())
            Render.cubeHighlight(poseStack, vertices, camera, lastTarget.getA());
    }
}
