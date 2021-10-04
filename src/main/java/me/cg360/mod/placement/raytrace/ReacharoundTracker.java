package me.cg360.mod.placement.raytrace;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class ReacharoundTracker {

    public static final String PLACE_TEXT_VERTICAL = "| + |";
    public static final String PLACE_TEXT_HORIZONTAL = "- + -";

    public static double leniency = 0.8; //TODO: Make this configurable.
    public static boolean verticalOrientation = true;

    public static Pair<BlockPos, Direction> currentTarget = null;
    public static int ticksDisplayed = 0;



    public static Pair<BlockPos, Direction>  getPlayerReacharoundTarget(PlayerEntity player) {

        // Check if either stack can be placed, else don't show the guide.
        if(!(isSupportedStack(player.getMainHandStack()) || isSupportedStack(player.getOffHandStack()))) return null;

        Pair<Vec3d, Vec3d> rayDetails = RayTraceHandler.getEntityParams(player);
        World world = player.world;

        double range = RayTraceHandler.getEntityRange(player);
        Vec3d rayPos = rayDetails.getLeft();
        Vec3d ray = rayDetails.getRight().multiply(range);

        HitResult regularCollision = RayTraceHandler.rayTrace(player, world, rayPos, ray, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE);

        // If there is not a normal block for the player to hit, attempt to raycast
        // reacharound targets.
        if (regularCollision.getType() == HitResult.Type.MISS) {

            Pair<BlockPos, Direction>  target = getVerticalTarget(player, world, rayPos, ray);
            if(target != null) {
                verticalOrientation = true;
                return target;
            }

            target = getHorizontalTarget(player, world, rayPos, ray);
            if(target != null) {
                verticalOrientation = false;
                return target;
            }
        }

        return null;
    }

    private static Pair<BlockPos, Direction> getVerticalTarget(PlayerEntity player, World world, Vec3d rayPos, Vec3d ray) {
        if(player.getPitch() < 0) return null;

        rayPos = rayPos.add(new Vec3d(0, leniency, 0));
        HitResult take2Res = RayTraceHandler.rayTrace(player, world, rayPos, ray, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE);

        if (take2Res.getType() == HitResult.Type.BLOCK && take2Res instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult) take2Res).getBlockPos().down();
            BlockState state = world.getBlockState(pos);

            if (player.getPos().y - pos.getY() > 1 && (world.isAir(pos) || state.getMaterial().isReplaceable()))
                return new Pair<>(pos, Direction.DOWN);
        }

        return null;
    }

    private static Pair<BlockPos, Direction> getHorizontalTarget(PlayerEntity player, World world, Vec3d rayPos, Vec3d ray) {
        Direction dir = Direction.fromRotation(player.headYaw);
        rayPos = rayPos.add(new Vec3d(-(leniency * dir.getOffsetX()), 0, -(leniency * dir.getOffsetZ())));
        HitResult take2Res = RayTraceHandler.rayTrace(player, world, rayPos, ray, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE);

        if (take2Res.getType() == HitResult.Type.BLOCK && take2Res instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult) take2Res).getBlockPos().offset(dir);
            BlockState state = world.getBlockState(pos);

            if ((world.isAir(pos) || state.getMaterial().isReplaceable())) return new Pair<>(pos, dir.getOpposite());
        }

        return null;
    }

    private static boolean isSupportedStack(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BlockItem;
    }

}
