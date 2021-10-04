package me.cg360.mod.placement.mixin;

import com.mojang.datafixers.util.Function4;
import me.cg360.mod.placement.PlacementMod;
import me.cg360.mod.placement.raytrace.ReacharoundTracker;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
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

    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

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


    @Inject(at = @At("HEAD"), method = "doItemUse()V", cancellable = true)
    public void onItemUse(CallbackInfo info) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if(player != null) {

            for(Hand hand : Hand.values()) {
                ItemStack stack = player.getStackInHand(hand);
                Pair<BlockPos, Direction> pair = ReacharoundTracker.getPlayerReacharoundTarget(player);

                if (pair != null) {
                    BlockPos pos = pair.getLeft();
                    Direction dir = pair.getRight();

                    if (!player.canPlaceOn(pos, dir, stack)) return;
                    int count = stack.getCount();

                    BlockHitResult result = new BlockHitResult(new Vec3d(0.5F, 1F, 0.5F), dir, pos, false);
                    ItemUsageContext context = new ItemUsageContext(player, hand, result);
                    boolean remote = !player.world.isClient;
                    Item item = stack.getItem();
                    ActionResult res = remote ? ActionResult.SUCCESS : item.useOnBlock(context);

                    if (res != ActionResult.PASS) {

                        if (res == ActionResult.SUCCESS){
                            player.swingHand(hand);

                            if(this.getNetworkHandler() != null) {
                                this.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(hand, result));
                            }

                        } else if (res == ActionResult.CONSUME) {
                            BlockPos placedPos = pos;
                            BlockState state = player.world.getBlockState(placedPos);
                            BlockSoundGroup soundType = state.getSoundGroup();

                            if(player.world instanceof ServerWorld) {
                                ((ServerWorld) player.world).playSound(null, placedPos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                            }

                        }

                        if (player.isCreative() && stack.getCount() < count && !remote) stack.setCount(count);
                    }
                }
            }
        }
    }

}
