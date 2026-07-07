package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.compat.type.SpecialBridgingEnvironmentHandler;
import me.cg360.mod.bridging.raytrace.BridgingResult;
import me.cg360.mod.bridging.raytrace.PlacementAlignment;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Gui.class)
public class CrosshairRenderingMixin {

    @Unique
    private static final int ICON_SIZE = 32;

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private DebugScreenOverlay debugOverlay;

    @Inject(method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At(value = "TAIL"))
    public void renderPlacementAssistMarker(GuiGraphics gui, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(BridgingStateTracker.getLastTickTarget() == null) return;
        if(this.minecraft.options.hideGui) return;
        if(!BridgingMod.getConfig().shouldShowCrosshair()) return;

        boolean isBridgingActive = BridgingMod.getConfig().isBridgingEnabled() &&
                                   (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || GameSupport.isControllerCrouching());

        if(!isBridgingActive)
            return;

        BridgingResult result = BridgingStateTracker.getLastTickTarget();
        boolean forceHideCrosshair = SpecialHandlers.getSpecialEnvironmentHandlers().stream().anyMatch(handler -> handler.forceHideCrosshair(result));

        if(forceHideCrosshair)
            return;

        Direction direction = result.direction();
        PlacementAlignment alignment = PlacementAlignment.from(direction);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        int w = gui.guiWidth();
        int h = gui.guiHeight();

        if(alignment == null) return;

        int x = ((w - ICON_SIZE + 1) / 2);
        int y = ((h - ICON_SIZE + 1) / 2);

        y += bridgingmod$getModCompatCrosshairHeight();
        y += this.debugOverlay.showDebugScreen() ? 15 : 0;

        gui.blitSprite(
                alignment.getTexturePath(),
                x, y,
                ICON_SIZE, ICON_SIZE
        );

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    @Unique
    private static int bridgingmod$getModCompatCrosshairHeight() {
        boolean wasModified = false;
        int currentShift = 0;

        for (SpecialBridgingEnvironmentHandler handler: SpecialHandlers.getSpecialEnvironmentHandlers()) {
            Optional<Integer> optShift = handler.modifyCrosshairHeight(currentShift, wasModified);

            if (optShift.isPresent()) {
                currentShift = optShift.get();
                wasModified = true;
            }
        }

        return currentShift;
    }

}
