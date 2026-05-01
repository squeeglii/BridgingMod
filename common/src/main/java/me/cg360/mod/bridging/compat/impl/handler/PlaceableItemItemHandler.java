package me.cg360.mod.bridging.compat.impl.handler;

import me.cg360.mod.bridging.compat.type.SpecialBridgingItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PlaceableItemItemHandler implements SpecialBridgingItemHandler {

    public static final PlaceableItemItemHandler INSTANCE = new PlaceableItemItemHandler();

    @Override
    public boolean canBePlaced(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canBePlacedInWorld(ItemStack stack, Player player, Level level, BlockPos pos, Direction direction) {
        return true; // just blind trust on this one. Recommended you test this.
    }

}
