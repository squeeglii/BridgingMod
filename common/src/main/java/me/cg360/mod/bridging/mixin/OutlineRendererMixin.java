package me.cg360.mod.bridging.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
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

    @Inject(method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
            at = @At("RETURN"))
    public void renderTracedViewPath(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        VertexConsumer vertices = this.renderBuffers.bufferSource().getBuffer(RenderType.lines());
        LocalPlayer player = this.minecraft.player;

        boolean isPlayerCrouching = player != null && player.isCrouching();
        boolean isBridgingEnabled = BridgingMod.getConfig().isBridgingEnabled() &&
                                    (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || isPlayerCrouching);

        if(!isBridgingEnabled)
            return;

        boolean isInDebugMenu = this.minecraft.options.renderDebug;
        boolean shouldRenderOutline = (isInDebugMenu  && BridgingMod.getConfig().shouldShowOutlineInF3()) ||
                (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutline());

        if(isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace())
            Render.blocksInViewPath(poseStack, vertices, camera);

        if(shouldRenderOutline) {
            Tuple<BlockPos, Direction> lastTarget = BridgingStateTracker.getLastTickTarget();

            if(lastTarget == null)
                return;

            int outlineColour = BridgingMod.getConfig().getOutlineColour();

            Render.cubeOutline(poseStack, vertices, camera, lastTarget.getA(), outlineColour);
        }
    }
}
