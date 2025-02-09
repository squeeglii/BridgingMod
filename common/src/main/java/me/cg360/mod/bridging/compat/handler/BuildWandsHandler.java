package me.cg360.mod.bridging.compat.handler;

import com.mojang.logging.LogUtils;
import me.cg360.mod.bridging.building.Bridge;
import me.cg360.mod.bridging.compat.SpecialBridgingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.natte.bankstorage.util.Util;
import net.nicguzzo.wands.items.WandItem;
import net.nicguzzo.wands.utils.Compat;
import net.nicguzzo.wands.utils.WandUtils;
import net.nicguzzo.wands.wand.PlayerWand;
import net.nicguzzo.wands.wand.Wand;
import net.nicguzzo.wands.wand.WandProps;

public class BuildWandsHandler implements SpecialBridgingHandler {

    public static final BuildWandsHandler INSTANCE = new BuildWandsHandler();

    @Override
    public boolean canBePlaced(ItemStack stack) {
        if(!WandUtils.is_wand(stack)) {
            // not a wand. This compatibility class does nothing!
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            LogUtils.getLogger().warn("Blocked using BuildingWands compatibility for an unsupported item (%s)!".formatted(id));
            return false;
        }

        return WandProps.getAction(stack) == WandProps.Action.PLACE;
    }

    @Override
    public boolean canBePlacedInWorld(ItemStack stack, Player player, Level level, BlockPos pos, Direction direction) {
        Wand wand = PlayerWand.get(player);

        if(wand == null) {
            LogUtils.getLogger().warn("Player is not holding building wand.");
            return false;
        }

        return player.mayBuild();
    }

    @Override
    public BlockHitResult generatePlacementTarget(ItemStack stack, Player player, Level level, Direction direction, BlockPos pos) {

        BlockHitResult result = Bridge.getDefaultPlaceAssistTarget(stack, level, direction, pos);

        LogUtils.getLogger().info("hit result: {}, {}, {}", result.getBlockPos(), result.getLocation(), result.getDirection());

        return result;
    }

}
