package me.cg360.mod.bridging.mixin;

import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {
        BridgingStateTracker.lastTickTarget = BridgingStateTracker.getBridgeAssistTargetFor(this.player);
    }


    @Inject(at = @At("HEAD"), method = "startUseItem()V")
    public void onItemUse(CallbackInfo info) {
        if(this.player == null)
            return;

        if(this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS)
            return;

        for(InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = this.player.getItemInHand(hand);

            Tuple<BlockPos, Direction> pair = BridgingStateTracker.lastTickTarget;

            if (pair == null) continue;

            BlockPos pos = pair.getA();
            Direction dir = pair.getB();

            if (!this.player.mayUseItemAt(pos, dir, itemStack)) return;
            if(this.gameMode == null) return;

            Vec3 startPos = Vec3.atCenterOf(pos);

            BlockHitResult blockHitResult = new BlockHitResult(startPos, dir, pos, false);

            int originalStackSize = itemStack.getCount();
            InteractionResult blockPlaceResult = this.gameMode.useItemOn(this.player, hand, blockHitResult);

            if (!blockPlaceResult.consumesAction()) continue;
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
