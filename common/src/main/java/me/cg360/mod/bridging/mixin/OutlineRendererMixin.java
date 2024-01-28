package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
public abstract class OutlineRendererMixin {

    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            ))
    public void renderTracedViewPath(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;

        boolean isPlayerCrouching = player != null && player.isCrouching();
        boolean isBridgingEnabled = BridgingMod.getConfig().isBridgingEnabled() &&
                                    (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || isPlayerCrouching);

        if(!isBridgingEnabled)
            return;

        boolean isInDebugMenu = this.minecraft.options.renderDebug;
        boolean shouldRenderOutline = (isInDebugMenu  && BridgingMod.getConfig().shouldShowOutlineInF3()) ||
                (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutline());

        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        VertexConsumer vertices = bufferSource.getBuffer(RenderType.lines());


        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, camera);

        if(shouldRenderOutline) {
            Tuple<BlockPos, Direction> lastTarget = BridgingStateTracker.getLastTickTarget();

            if(lastTarget == null)
                return;

            int outlineColour = BridgingMod.getConfig().getOutlineColour();

            Render.cubeOutline(poseStack, vertices, camera, lastTarget.getA(), outlineColour);
        }
    }
}
