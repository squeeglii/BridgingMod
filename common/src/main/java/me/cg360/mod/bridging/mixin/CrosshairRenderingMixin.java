package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.BridgingCrosshairTweaks;
import me.cg360.mod.bridging.raytrace.PlacementAlignment;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;

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
        if(BridgingCrosshairTweaks.forceHidden) return;
        if(this.minecraft.options.hideGui) return;

        if(!BridgingMod.getConfig().shouldShowCrosshair()) return;

        boolean isBridgingActive = BridgingMod.getConfig().isBridgingEnabled() &&
                                   (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || GameSupport.isControllerCrouching());

        if(!isBridgingActive)
            return;

        Direction direction = BridgingStateTracker.getLastTickTarget().getB();
        PlacementAlignment alignment = PlacementAlignment.from(direction);

        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
        GpuTexture texColour = renderTarget.getColorTexture();
        GpuTexture texDepth = renderTarget.getDepthTexture();

        try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(texColour, OptionalInt.empty(), texDepth, OptionalDouble.empty())) {

            pass.setPipeline(RenderPipelines.CROSSHAIR);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int w = gui.guiWidth();
            int h = gui.guiHeight();

            if(alignment == null) return;

            int x = ((w - ICON_SIZE + 1) / 2);
            int y = ((h - ICON_SIZE + 1) / 2);

            y += BridgingCrosshairTweaks.yShift;
            y += this.debugOverlay.showDebugScreen() ? 15 : 0;

            gui.blitSprite(
                    RenderType::crosshair,
                    alignment.getTexturePath(),
                    x, y,
                    ICON_SIZE, ICON_SIZE
            );
        }


    }

}
