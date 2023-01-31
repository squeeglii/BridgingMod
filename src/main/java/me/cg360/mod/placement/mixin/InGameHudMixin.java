package me.cg360.mod.placement.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.cg360.mod.placement.PlacementMod;
import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final int ICON_SIZE = 31;

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(method = "renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderPlacementAssistText(MatrixStack matrices, CallbackInfo ci) {

        if(ReacharoundTracker.currentTarget != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, PlacementMod.PLACEMENT_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

            int w = this.scaledWidth;
            int h = this.scaledHeight;

            if (ReacharoundTracker.isInVerticalOrientation()) {
                ((InGameHud) (Object) this).drawTexture(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE);

            } else {
                ((InGameHud) (Object) this).drawTexture(matrices, ((w - ICON_SIZE) / 2), (h - ICON_SIZE) / 2, 32, 0, ICON_SIZE, ICON_SIZE);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        }
    }

}
