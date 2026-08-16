package me.cg360.mod.bridging.entrypoint.forge;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.compat.BridgingCrosshairTweaks;
import me.cg360.mod.bridging.raytrace.PlacementAlignment;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.InfoStrings;
import me.cg360.mod.bridging.util.Path;
import me.cg360.mod.bridging.util.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BridgingForgeClientHooks {

    private static final int ICON_SIZE = 31;

    private boolean lastUseKeyDown = false;
    private int useDelayTicks = 0;
    private double lastKnownYFrac = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null) return;

        if (minecraft.player.onGround()) {
            this.lastKnownYFrac = Mth.frac(minecraft.player.getY());
        }

        if (BridgingKeyMappings.TOGGLE_BRIDGING.consumeClick()) {
            BridgingMod.getConfig().toggleBridgingEnabled();

            Component stateMsg = BridgingMod.getConfig().isBridgingEnabled()
                    ? InfoStrings.ON
                    : InfoStrings.OFF;
            Component text = InfoStrings.TOGGLE_BRIDGING.copy().append(stateMsg);
            minecraft.gui.setOverlayMessage(text, false);
        }

        BridgingStateTracker.tick(minecraft.player);

        if (minecraft.screen != null) {
            this.lastUseKeyDown = false;
            return;
        }

        if (this.useDelayTicks > 0) {
            this.useDelayTicks--;
        }

        boolean useKeyDown = minecraft.options.keyUse.isDown();

        if (!useKeyDown) {
            this.lastUseKeyDown = false;
            return;
        }

        if (!BridgingMod.getConfig().isBridgingEnabled()) {
            this.lastUseKeyDown = true;
            return;
        }

        if (this.useDelayTicks > 0) {
            this.lastUseKeyDown = true;
            return;
        }

        if (minecraft.player.isHandsBusy() || minecraft.gameMode.isDestroying()) {
            this.lastUseKeyDown = true;
            return;
        }

        if (minecraft.hitResult != null && minecraft.hitResult.getType() != HitResult.Type.MISS) {
            this.lastUseKeyDown = true;
            return;
        }

        boolean passesCrouchTest = !BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || minecraft.player.isCrouching();
        if (!passesCrouchTest) {
            this.lastUseKeyDown = true;
            return;
        }

        boolean placed = this.tryPlaceFromBridgeAssist(minecraft);
        this.lastUseKeyDown = true;

        if (placed) {
            this.useDelayTicks = Math.max(0, BridgingMod.getConfig().getDelayPostBridging());
        }
    }

    private boolean tryPlaceFromBridgeAssist(Minecraft minecraft) {
        Tuple<BlockPos, Direction> pair = BridgingStateTracker.getLastTickTarget();
        if (pair == null) return false;

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = minecraft.player.getItemInHand(hand);
            if (!GameSupport.isStackPlaceable(itemStack)) continue;

            BlockPos pos = pair.getA();
            Direction dir = pair.getB();

            BlockHitResult target = this.getFinalPlaceAssistTarget(minecraft, itemStack, dir, pos);
            int originalStackSize = itemStack.getCount();
            InteractionResult result = minecraft.gameMode.useItemOn(minecraft.player, hand, target);

            if (!result.consumesAction()) {
                Direction fallbackDir = dir.getOpposite();
                if (fallbackDir != dir) {
                    BlockHitResult fallbackTarget = this.getFinalPlaceAssistTarget(minecraft, itemStack, fallbackDir, pos);
                    result = minecraft.gameMode.useItemOn(minecraft.player, hand, fallbackTarget);
                }
            }

            if (!result.consumesAction()) continue;

            if (result.shouldSwing()) {
                minecraft.player.swing(hand);
            }

            boolean stackSizeChanged = itemStack.getCount() != originalStackSize || minecraft.gameMode.hasInfiniteItems();
            if (stackSizeChanged && !itemStack.isEmpty()) {
                minecraft.gameRenderer.itemInHandRenderer.itemUsed(hand);
            }

            return true;
        }

        return false;
    }

    private BlockHitResult getFinalPlaceAssistTarget(Minecraft minecraft, ItemStack heldItem, Direction dir, BlockPos pos) {
        if (BridgingMod.getConfig().isSlabAssistEnabled()) {
            BlockHitResult override = switch (dir.getAxis()) {
                case X, Z -> this.handleHorizontalSlabAssist(pos);
                case Y -> this.handleVerticalSlabAssist(minecraft, heldItem, dir, pos);
            };

            if (override != null) return override;
        }

        return new BlockHitResult(Vec3.atCenterOf(pos), dir, pos, true);
    }

    private BlockHitResult handleHorizontalSlabAssist(BlockPos pos) {
        boolean shouldTargetLowerHalf = this.lastKnownYFrac > GameSupport.TRAPDOOR_HEIGHT - Path.NEAR_ZERO
                && this.lastKnownYFrac < GameSupport.SLAB_HEIGHT + Path.NEAR_ZERO;

        Vec3 placerOrigin = shouldTargetLowerHalf
                ? Vec3.atBottomCenterOf(pos).add(0, 0.1d, 0)
                : Vec3.atBottomCenterOf(pos).add(0, 0.9d, 0);

        Direction placeDir = shouldTargetLowerHalf ? Direction.UP : Direction.DOWN;
        return new BlockHitResult(placerOrigin, placeDir, pos, false);
    }

    private BlockHitResult handleVerticalSlabAssist(Minecraft minecraft, ItemStack heldItem, Direction dir, BlockPos pos) {
        if (minecraft.level == null) return null;
        if (!(heldItem.getItem() instanceof BlockItem blockItem)) return null;
        if (!(blockItem.getBlock() instanceof SlabBlock)) return null;

        BlockPos buildingOffPos = pos.offset(dir.getNormal().multiply(-1));
        BlockState localState = minecraft.level.getBlockState(buildingOffPos);

        if (!(localState.getBlock() instanceof SlabBlock)) return null;
        SlabType slabType = localState.getValue(SlabBlock.TYPE);

        if (slabType == SlabType.DOUBLE) return null;
        if (slabType == SlabType.TOP && dir != Direction.DOWN) return null;
        if (slabType == SlabType.BOTTOM && dir != Direction.UP) return null;

        return new BlockHitResult(Vec3.atCenterOf(pos), dir, buildingOffPos, false);
    }

    // Safety-net replacement for OutlineRendererMixin, in case the mixin still fails to apply.
    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft minecraft = Minecraft.getInstance();
        boolean isInDebugMenu = minecraft.getDebugOverlay().showDebugScreen();

        boolean isBridgingEnabled = BridgingMod.getConfig().isBridgingEnabled() &&
                (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || GameSupport.isControllerCrouching());

        boolean shouldRenderOutline = (isInDebugMenu && BridgingMod.getConfig().shouldShowOutlineInF3()) ||
                (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutline());
        boolean isOutlineEnabled = shouldRenderOutline && isBridgingEnabled;

        boolean shouldRenderNonBridgeOutline = (isInDebugMenu && BridgingMod.getConfig().shouldShowNonBridgeOutlineInF3()) ||
                (!isInDebugMenu && BridgingMod.getConfig().shouldShowOutlineEvenWhenNotBridging());
        boolean isNonBridgeOutlineEnabled = shouldRenderNonBridgeOutline &&
                (isBridgingEnabled || !BridgingMod.getConfig().shouldNonBridgeRespectsCrouchRules());

        if (!(isOutlineEnabled || isNonBridgeOutlineEnabled)) return;

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertices = bufferSource.getBuffer(RenderType.lines());
        PoseStack poseStack = new PoseStack();

        if (isInDebugMenu && BridgingMod.getConfig().shouldShowDebugTrace()) {
            Render.blocksInViewPath(poseStack, vertices, event.getCamera());
        }

        if (isOutlineEnabled) Render.currentBridgingOutline(poseStack, event.getCamera(), vertices);
        if (isNonBridgeOutlineEnabled) Render.currentNonBridgingOutline(poseStack, event.getCamera(), vertices);

        bufferSource.endBatch(RenderType.lines());
    }

    // KNOWN ISSUE: crosshair icon does not visibly change on this Forge build (52.1.x for 1.21.1).
    // Placement/outline both work; this render path fires but the icon swap is not visible in-game.
    // Not fixed - root cause unconfirmed (suspect texture/blend state fighting with vanilla crosshair
    // draw call, since this Forge build has no GuiOverlay API and mixins don't apply here either).
    // Manual crosshair overlay draw, since this Forge build has neither a working GuiOverlay API
    // nor a firing CrosshairRenderingMixin. Runs as the very last draw call of each frame.
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;
        if (minecraft.screen != null) return;
        if (minecraft.options.hideGui) return;
        if (BridgingStateTracker.getLastTickTarget() == null) return;
        if (!BridgingMod.getConfig().shouldShowCrosshair()) return;

        boolean isBridgingActive = BridgingMod.getConfig().isBridgingEnabled() &&
                (!BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() || GameSupport.isControllerCrouching());
        if (!isBridgingActive) return;

        Direction direction = BridgingStateTracker.getLastTickTarget().getB();
        PlacementAlignment alignment = PlacementAlignment.from(direction);
        if (alignment == null) return;

        GuiGraphics gui = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());

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

        int x = (w - ICON_SIZE) / 2;
        int y = (h - ICON_SIZE) / 2;

        y += minecraft.getDebugOverlay().showDebugScreen() ? 15 : 0;

        gui.blit(
                BridgingMod.PLACEMENT_ICONS_TEXTURE, x, y,
                alignment.getTextureOffset(), 0,
                ICON_SIZE, ICON_SIZE
        );

        gui.flush();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
    }
}
