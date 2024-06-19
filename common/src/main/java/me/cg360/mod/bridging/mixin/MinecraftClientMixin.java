package me.cg360.mod.bridging.mixin;

import com.mojang.logging.LogUtils;
import me.cg360.mod.bridging.BridgingKeyMappings;
import me.cg360.mod.bridging.BridgingMod;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
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

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Unique
    private double bridgingmod$lastKnownYFrac = 0;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public HitResult hitResult;

    @Shadow private int rightClickDelay;

    @Shadow @Nullable public ClientLevel level;

    @Inject(at = @At("TAIL"), method = "tick()V")
    public void onTick(CallbackInfo ci) {

        if(this.player != null && this.player.onGround()) {
            this.bridgingmod$lastKnownYFrac = Mth.frac(this.player.getY());
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

            if (!this.player.mayUseItemAt(pos, dir, itemStack))
                continue;

            BlockHitResult blockHitResult = bridgingmod$getFinalPlaceAssistTarget(this.player, itemStack, dir, pos);

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

    @Unique
    @NotNull
    private BlockHitResult bridgingmod$getFinalPlaceAssistTarget(Player player, ItemStack heldItem, Direction dir, BlockPos pos) {
        // Where is the placement action coming from?
        // This is used by the game to determine the state used for directional blocks.

        if(BridgingMod.getConfig().isSlabAssistEnabled()) {
            BlockHitResult override = switch (dir.getAxis()) {
                case X, Z -> bridgingmod$handleHorizontalSlabAssist(player, dir, pos);
                case Y -> bridgingmod$handleVerticalSlabAssist(heldItem, dir, pos);
            };

            if(override != null) return override;
        }

        Vec3 placerOrigin = Vec3.atCenterOf(pos);
        return new BlockHitResult(placerOrigin, dir, pos, true);
    }

    @Unique
    private BlockHitResult bridgingmod$handleHorizontalSlabAssist(Player player, Direction dir, BlockPos pos) {
        // Slab assist should also help with trapdoors.
        // I would get stairs to work too but that either requires major jank
        // or server-side mods.

        boolean shouldTargetLowerHalf = this.bridgingmod$lastKnownYFrac > GameSupport.TRAPDOOR_HEIGHT - Path.NEAR_ZERO
                                     && this.bridgingmod$lastKnownYFrac < GameSupport.SLAB_HEIGHT + Path.NEAR_ZERO;

        Vec3 placerOrigin = shouldTargetLowerHalf
                ? Vec3.atBottomCenterOf(pos).add(0, 0.1d, 0)
                : Vec3.atBottomCenterOf(pos).add(0, 0.9d, 0);

        return new BlockHitResult(placerOrigin, dir, pos, true);
    }

    // When bridging up or down, using slabs on slabs, merge them into double slabs where possible so it looks nice :)
    @Unique
    private BlockHitResult bridgingmod$handleVerticalSlabAssist(ItemStack heldItem, Direction dir, BlockPos pos) {
        if(this.level == null) return null;
        if(!(heldItem.getItem() instanceof BlockItem blockItem)) return null;
        if(!(blockItem.getBlock() instanceof SlabBlock)) return null;

        // Get the block to place "upgrade" the slab from
        BlockPos buildingOffPos = pos.offset(dir.getNormal().multiply(-1));
        BlockState localState = this.level.getBlockState(buildingOffPos);

        if(!(localState.getBlock() instanceof SlabBlock)) return null;
        SlabType slabType = localState.getValue(SlabBlock.TYPE);

        // Check if the placement will even be accepted by the game.
        if(slabType == SlabType.DOUBLE) return null;
        if(slabType == SlabType.TOP && dir != Direction.DOWN) return null;
        if(slabType == SlabType.BOTTOM && dir != Direction.UP) return null;

        Vec3 placerOrigin = Vec3.atCenterOf(pos);
        return new BlockHitResult(placerOrigin, dir, buildingOffPos, false);
    }
}
