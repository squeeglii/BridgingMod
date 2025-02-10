package me.cg360.mod.bridging.mixin;

import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.building.Bridge;
import me.cg360.mod.bridging.compat.BridgingCrosshairTweaks;
import me.cg360.mod.bridging.compat.SpecialBridgingHandler;
import me.cg360.mod.bridging.compat.SpecialHandlers;
import me.cg360.mod.bridging.raytrace.BridgingStateTracker;
import me.cg360.mod.bridging.util.GameSupport;
import me.cg360.mod.bridging.util.InfoStrings;
import me.cg360.mod.bridging.util.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public HitResult hitResult;

    @Shadow private int rightClickDelay;

    @Shadow @Nullable public ClientLevel level;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {

        if(this.player != null && this.player.onGround()) {
            BridgingStateTracker.lastKnownYFrac = Mth.frac(this.player.getY());
        }

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
            Direction dir = pair.getB().getOpposite(); // Fixes placing on vertical axes -- doesn't affect most horizontal blocks for some reason.

            InteractionResult blockPlaceResult = null;
            int originalStackSize = itemStack.getCount();

            // Compatibility Api - allow custom handling of blocks.
            Optional<SpecialBridgingHandler> optHandler = SpecialHandlers.getSpecialHandler(itemStack);
            boolean canBePlaced, canBePlacedInWorld;

            if(optHandler.isPresent()) {
                SpecialBridgingHandler handler = optHandler.get();

                canBePlaced = handler.canBePlaced(itemStack);
                canBePlacedInWorld = handler.canBePlacedInWorld(itemStack, this.player, this.level, pos, dir);

                if(canBePlaced && canBePlacedInWorld) {
                    blockPlaceResult = optHandler.get().place();
                } else continue;

            } else {
                canBePlaced = GameSupport.passesDefaultPlacementCheck(itemStack);
                canBePlacedInWorld = this.player.mayUseItemAt(pos, dir, itemStack);
            }

            // No custom handling of blocks? Do it the default way.
            if(blockPlaceResult == null) {
                if (!(canBePlaced && canBePlacedInWorld))
                    continue;

                BlockHitResult blockHitResult = bridgingmod$getFinalPlaceAssistTarget(itemStack, dir, pos, optHandler.orElse(null));
                blockPlaceResult = this.gameMode.useItemOn(this.player, hand, blockHitResult);
            }

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

    @Unique
    @NotNull
    private BlockHitResult bridgingmod$getFinalPlaceAssistTarget(ItemStack heldItem, Direction dir, BlockPos pos, SpecialBridgingHandler specialHandler) {
        // Where is the placement action coming from?
        // This is used by the game to determine the state used for directional blocks.

        if(specialHandler != null) {
            BlockHitResult customPlaceAssistTarget = specialHandler.generatePlacementTarget(heldItem, this.player, this.level, dir, pos);

            if(customPlaceAssistTarget != null) {
                return customPlaceAssistTarget;
            }
        }

        return Bridge.getDefaultPlaceAssistTarget(heldItem, this.level, dir, pos);
    }

}
