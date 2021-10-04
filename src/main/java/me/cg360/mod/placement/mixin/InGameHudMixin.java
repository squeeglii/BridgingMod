package me.cg360.mod.placement.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private static final String PLACE_TEXT = "[+]";

    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    public void renderPlacementAssistText(MatrixStack matrices, float tickDelta, CallbackInfo ci) {

        if(ReacharoundTracker.currentTarget != null) {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

            matrices.push();
            {
                MinecraftClient.getInstance().textRenderer.draw(matrices, PLACE_TEXT, 0, 0, 0xFFFFFF);
                matrices.translate(((double) scaledWidth) / 2d, ((double) scaledHeight - 10) / 2d, 0); // Place the text in the center
                matrices.translate(-(MinecraftClient.getInstance().textRenderer.getWidth(PLACE_TEXT) / 2d), 0, 0); // Center based on width
            }
            matrices.pop();
        }
    }

}
