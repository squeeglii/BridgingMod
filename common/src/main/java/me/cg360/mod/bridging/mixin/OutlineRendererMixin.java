package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "renderBlockOutline",
            at = @At("HEAD")
            )
    public void renderTracedViewPath(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean bl, LevelRenderState levelRenderState, CallbackInfo ci) {
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

        VertexConsumer vertices = bufferSource.getBuffer(RenderType.lines());

        // Creating a fresh pose stack should be fine - the main pose stack is meant to be
        // empty before rendering the vanilla outline anyway.
        //PoseStack poseStack = new PoseStack();
        // what.


        SourcePerspective perspectiveLock = BridgingMod.getCompatibleSourcePerspective();
        Player player = Minecraft.getInstance().player;

        if(player == null)
            perspectiveLock = SourcePerspective.COPY_TOGGLE_PERSPECTIVE;

        Perspective view = switch (perspectiveLock) {
            case COPY_TOGGLE_PERSPECTIVE, LET_BRIDGING_MOD_DECIDE ->
                    Perspective.fromCamera(Minecraft.getInstance().gameRenderer.getMainCamera());

            case ALWAYS_EYELINE ->
                    Perspective.fromEntity(player);
        };

        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, view);

        if(isOutlineEnabled) Render.currentBridgingOutline(poseStack, view, vertices);
        if(isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, view, vertices);

        this.checkPoseStack(poseStack);
    }

}
