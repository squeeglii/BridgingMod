package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.ReacharoundTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class CrosshairRenderingMixin {

    private static final int ICON_SIZE = 31;

    @Shadow private int screenHeight;
    @Shadow private int screenWidth;

    @Inject(method = "renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderPlacementAssistText(PoseStack matrices, CallbackInfo ci) {

        if(ReacharoundTracker.currentTarget != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BridgingMod.PLACEMENT_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            int w = this.screenHeight;
            int h = this.screenWidth;

            if (ReacharoundTracker.isInVerticalOrientation()) {
                GuiComponent.blit(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE);

            } else {
                GuiComponent.blit(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 32, 0, ICON_SIZE, ICON_SIZE);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        }
    }

}
