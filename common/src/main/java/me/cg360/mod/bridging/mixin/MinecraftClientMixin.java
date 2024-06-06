package me.cg360.mod.bridging.mixin;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.InfoStrings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public HitResult hitResult;

    @Shadow private int rightClickDelay;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {
        if(BridgingKeyMappings.TOGGLE_BRIDGING.consumeClick()) {
            BridgingMod.getConfig().toggleBridgingEnabled();

            Component stateMsg = BridgingMod.getConfig().isBridgingEnabled()
                    ? InfoStrings.ON
                    : InfoStrings.OFF;
            Component text = InfoStrings.TOGGLE_BRIDGING.copy().append(stateMsg);
            Minecraft.getInstance().gui.setOverlayMessage(text, false);
        }

        BridgingStateTracker.tick(this.player);
    }


    @Inject(at = @At("HEAD"), method = "startUseItem()V", cancellable = true)
    public void onItemUse(CallbackInfo info) {
        if(!BridgingMod.getConfig().isBridgingEnabled()) return;
        if(this.player == null) return;
        if(this.gameMode == null) return;
        if(this.player.isHandsBusy() || this.gameMode.isDestroying()) return;

        // Should only bridge if all other options to interact are exhausted
        if(this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) return;

        boolean passesCrouchTest = !BridgingMod.getConfig().shouldOnlyBridgeWhenCrouched() ||
                                    this.player.isCrouching();

        if(!passesCrouchTest)
            return;

        Tuple<BlockPos, Direction> pair = BridgingStateTracker.getLastTickTarget();

        if (pair == null) return;

        for(InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = this.player.getItemInHand(hand);

            BlockPos pos = pair.getA();
            Direction dir = pair.getB();

            if (!this.player.mayUseItemAt(pos, dir, itemStack))
                continue;

            double deltaY = this.player.getY() - 0.01d - Vec3.atCenterOf(pos).y();
            double clamped = Mth.clamp(deltaY, -0.5d, 0.5d);

            Vec3 startPos = BridgingMod.getConfig().isSlabAssistEnabled()
                    ? Vec3.atCenterOf(pos).add(0, clamped, 0)
                    : Vec3.atCenterOf(pos);
            BlockHitResult blockHitResult = new BlockHitResult(startPos, dir, pos, true);

            int originalStackSize = itemStack.getCount();
            InteractionResult blockPlaceResult = this.gameMode.useItemOn(this.player, hand, blockHitResult);

            if (!blockPlaceResult.consumesAction()) continue;

            // if successful place occurred, cancel all future behaviour for
            // item placement as this takes over instead. Stops off-hand
            // shields from firing constantly.
            this.rightClickDelay = Math.max(0, BridgingMod.getConfig().getDelayPostBridging());
            info.cancel();

            if (!blockPlaceResult.shouldSwing()) return;

            this.player.swing(hand);
            boolean stackSizeChanged = itemStack.getCount() != originalStackSize || this.gameMode.hasInfiniteItems();

            if (stackSizeChanged && !itemStack.isEmpty()) {
                Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
            }

            return;
        }
    }
}
