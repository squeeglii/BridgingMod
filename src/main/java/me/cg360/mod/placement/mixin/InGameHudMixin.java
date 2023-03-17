package me.cg360.mod.placement.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.cg360.mod.placement.PlacementMod;
import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    private static final int ICON_SIZE = 31;

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderPlacementAssistText(PoseStack matrices, CallbackInfo ci) {

        if(ReacharoundTracker.currentTarget != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, PlacementMod.PLACEMENT_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            int w = this.scaledWidth;
            int h = this.scaledHeight;

            if (ReacharoundTracker.isInVerticalOrientation()) {
                ((Gui) (Object) this).blit(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE);

            } else {
                ((Gui) (Object) this).blit(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 32, 0, ICON_SIZE, ICON_SIZE);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        }
    }

}
