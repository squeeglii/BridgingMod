package me.cg360.mod.placement.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final int FADE_IN_TICKS = 40;

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Shadow private int ticks;

    @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderPlacementAssistText(MatrixStack matrices, CallbackInfo ci) {

        if(ReacharoundTracker.currentTarget != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Identifier.of("placementpog", "textures/gui/placement_icons.png"));
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

            int w = this.scaledWidth;
            int h = this.scaledHeight;

            if (ReacharoundTracker.isInVerticalOrientation()) {
                ((InGameHud) (Object) this).drawTexture(matrices, ((w - 24) / 2) + 30, (h - 24) / 2, 0, 0, 24, 24);

            } else {
                ((InGameHud) (Object) this).drawTexture(matrices, ((w - 24) / 2) + 30, (h - 24) / 2, 24, 0, 24, 24);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        }
    }

}
