package me.cg360.mod.bridging.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.Render;
import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.LevelRenderState;
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
    public void renderTracedViewPath(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean bl, LevelRenderState levelRenderState, CallbackInfo ci, @Local(ordinal = 0) float f) {
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

        VertexConsumer vertices = bufferSource.getBuffer(RenderTypes.lines());

        // Creating a fresh pose stack should be fine - the main pose stack is meant to be
        // empty before rendering the vanilla outline anyway.
        //PoseStack poseStack = new PoseStack();
        // what.


        Player player = Minecraft.getInstance().player;

        // There may be a few cases where this would be useful to still show bridging for,
        // but that makes headaches.
        if (player == null)
            return;

        Perspective view = Perspective.getSourcePerspective(player);

        BridgingPreContext preContext = new BridgingPreContext(
                player.level(),
                view,
                Perspective.fromEntity(player),
                player,
                Flags.empty()
        );

        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, preContext);

        if(isOutlineEnabled) Render.currentBridgingOutline(poseStack, vertices, f);
        if(isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, view, vertices);

        this.checkPoseStack(poseStack);
    }

}
