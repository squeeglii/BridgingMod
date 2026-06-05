package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.render.Render;
import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.player.Player;
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
    public void renderTracedViewPath(DeltaTracker deltaTracker, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
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

        SourcePerspective perspectiveLock = BridgingMod.getCompatibleSourcePerspective();
        Player player = Minecraft.getInstance().player;

        // There may be a few cases where this would be useful to still show bridging for,
        // but that makes headaches.
        if (player == null)
            return;

        Perspective view = switch (perspectiveLock) {
            case COPY_TOGGLE_PERSPECTIVE, LET_BRIDGING_MOD_DECIDE ->
                    Perspective.fromCamera(Minecraft.getInstance().gameRenderer.getMainCamera());

            case ALWAYS_EYELINE ->
                    Perspective.fromEntity(player);
        };

        BridgingPreContext preContext = new BridgingPreContext(
                player.level(),
                view,
                Perspective.fromEntity(player),
                player,
                Flags.empty()
        );

        // Creating a fresh pose stack should be fine - the main pose stack is meant to be
        // empty before rendering the vanilla outline anyway. -1.21.1
        PoseStack poseStack = new PoseStack();

        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, preContext);

        if(isOutlineEnabled) Render.currentBridgingOutline(poseStack, view, vertices);
        if(isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, view, vertices);

        this.checkPoseStack(poseStack);
    }

}
