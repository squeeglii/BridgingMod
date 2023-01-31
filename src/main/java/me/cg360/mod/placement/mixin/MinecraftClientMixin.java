package me.cg360.mod.placement.mixin;

import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {
        ReacharoundTracker.currentTarget = null;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player != null) {
            ReacharoundTracker.currentTarget = ReacharoundTracker.getPlayerReacharoundTarget(player);
        }

        if(ReacharoundTracker.currentTarget != null) {
            if(ReacharoundTracker.ticksDisplayed < 5) ReacharoundTracker.ticksDisplayed++;

        } else {
            ReacharoundTracker.ticksDisplayed = 0;
        }
    }


    @Inject(at = @At("HEAD"), method = "doItemUse()V")
    public void onItemUse(CallbackInfo info) {
        if(this.player != null) {

            for(Hand hand : Hand.values()) {
                ItemStack itemStack = this.player.getStackInHand(hand);
                Pair<BlockPos, Direction> pair = ReacharoundTracker.getPlayerReacharoundTarget(this.player);

                if (pair != null) {
                    BlockPos pos = pair.getLeft();
                    Direction dir = pair.getRight();

                    if (!this.player.canPlaceOn(pos, dir, itemStack)) return;
                    if(this.interactionManager == null) return;

                    BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(0, 1F, 0).add(Vec3d.ofCenter(pos)), dir, pos, false);

                    int i = itemStack.getCount();
                    ActionResult blockPlaceResult = this.interactionManager.interactBlock(this.player, hand, blockHitResult);

                    if (blockPlaceResult.isAccepted()) {
                        if (blockPlaceResult.shouldSwingHand()) {
                            this.player.swingHand(hand);
                            if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
                                MinecraftClient.getInstance().gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                            }
                        }

                        return;
                    }
                }
            }
        }
    }
}
