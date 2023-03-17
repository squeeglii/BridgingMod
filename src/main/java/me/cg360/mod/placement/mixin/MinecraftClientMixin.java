package me.cg360.mod.placement.mixin;

import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public MultiPlayerGameMode interactionManager;

    @Shadow @Nullable public LocalPlayer player;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {
        ReacharoundTracker.currentTarget = null;

        Player player = Minecraft.getInstance().player;
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

            for(InteractionHand hand : InteractionHand.values()) {
                ItemStack itemStack = this.player.getItemInHand(hand);
                Tuple<BlockPos, Direction> pair = ReacharoundTracker.getPlayerReacharoundTarget(this.player);

                if (pair != null) {
                    BlockPos pos = pair.getA();
                    Direction dir = pair.getB();

                    if (!this.player.mayUseItemAt(pos, dir, itemStack)) return;
                    if(this.interactionManager == null) return;

                    BlockHitResult blockHitResult = new BlockHitResult(new Vec3(0, 1F, 0).add(Vec3.atCenterOf(pos)), dir, pos, false);

                    int i = itemStack.getCount();
                    InteractionResult blockPlaceResult = this.interactionManager.useItemOn(this.player, hand, blockHitResult);

                    if (blockPlaceResult.consumesAction()) {
                        if (blockPlaceResult.shouldSwing()) {
                            this.player.swing(hand);
                            if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasInfiniteItems())) {
                                Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
                            }
                        }

                        return;
                    }
                }
            }
        }
    }
}
