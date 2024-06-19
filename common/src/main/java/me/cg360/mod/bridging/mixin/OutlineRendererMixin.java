package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
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

    @Shadow protected abstract void checkPoseStack(PoseStack poseStack);

    @Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            ))
    public void renderTracedViewPath(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matA, Matrix4f matB, CallbackInfo ci) {
        boolean isInDebugMenu = this.minecraft.getDebugOverlay().showDebugScreen();

        // Rules to display any bridging - whether these are followed or not depends on the config :)
        boolean isBridgingEnabled = BridgingMod.getConfig().isBridgingEnabled() &&
                                    (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || GameSupport.isControllerCrouching());

        boolean shouldRenderOutline = (isInDebugMenu  && BridgingMod.getConfig().shouldShowOutlineInF3()) ||
                                      (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutline());
        boolean isOutlineEnabled = shouldRenderOutline && isBridgingEnabled;

        boolean shouldRenderNonBridgeOutline = (isInDebugMenu  && BridgingMod.getConfig().shouldShowNonBridgeOutlineInF3()) ||
                                               (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutlineEvenWhenNotBridging());
        boolean isNonBridgeOutlineEnabled = shouldRenderNonBridgeOutline &&
                                            (isBridgingEnabled || !BridgingMod.getConfig().shouldNonBridgeRespectsCrouchRules());

        // Skip if nothing is valid to render.
        if(!(isOutlineEnabled || isNonBridgeOutlineEnabled))
            return;

        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        VertexConsumer vertices = bufferSource.getBuffer(RenderType.lines());

        // Creating a fresh pose stack should be fine - the main pose stack is meant to be
        // empty before rendering the vanilla outline anyway.
        PoseStack poseStack = new PoseStack();


        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, camera);

        if(isOutlineEnabled) Render.currentBridgingOutline(poseStack, camera, vertices);
        if(isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, camera, vertices);

        this.checkPoseStack(poseStack);
    }

}
