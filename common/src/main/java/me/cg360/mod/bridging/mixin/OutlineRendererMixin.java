package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingPreContext;
import me.cg360.mod.bridging.raytrace.Perspective;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.render.Render;
import me.cg360.mod.bridging.util.flags.Flags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class OutlineRendererMixin {

    @Shadow protected abstract void checkPoseStack(PoseStack poseStack);

    @Inject(method = "submitBlockOutline",
            at = @At("HEAD")
            )
    public void renderTracedViewPath(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LevelRenderState levelRenderState, CallbackInfo ci) {
        boolean isInDebugMenu = Minecraft.getInstance().getDebugOverlay().showDebugScreen();

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
            Render.blocksInViewPath(poseStack, submitNodeCollector, preContext);

        if(isOutlineEnabled) Render.currentBridgingOutline(poseStack, submitNodeCollector, 0f); //todo: where are partial ticks now.
        if(isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, view, submitNodeCollector);

        this.checkPoseStack(poseStack);
    }

}
