package me.cg360.mod.placement.raytrace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ReacharoundTracker {

    public static double leniency = 1f;
    private static boolean verticalOrientation = true;

    public static Tuple<BlockPos, Direction> currentTarget = null;
    public static int ticksDisplayed = 0;



    public static Tuple<BlockPos, Direction> getPlayerReacharoundTarget(Player player) {

        // Check if either stack can be placed, else don't show the guide.
        if(!(isSupportedStack(player.getMainHandItem()) || isSupportedStack(player.getOffhandItem()))) return null;

        Tuple<Vec3, Vec3> rayDetails = RayTraceHandler.getEntityParams(player);
        Level world = player.level;

        double range = RayTraceHandler.getEntityRange(player);
        Vec3 rayPos = rayDetails.getA();
        Vec3 ray = rayDetails.getB().scale(range);

        HitResult regularCollision = RayTraceHandler.rayTrace(player, world, rayPos, rayPos.add(ray), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);

        // If there is not a normal block for the player to hit, attempt to raycast
        // reacharound targets.
        if (regularCollision.getType() == HitResult.Type.MISS) {

            Tuple<BlockPos, Direction>  target = getVerticalTarget(player, world, rayPos, ray);
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

    private static Tuple<BlockPos, Direction> getVerticalTarget(Player player, Level world, Vec3 rayPos, Vec3 ray) {
        if(player.getXRot() < 0) return null;

        Vec3 endPos = rayPos.add(new Vec3(0, leniency, 0)).add(ray);
        HitResult take2Res = RayTraceHandler.rayTrace(player, world, rayPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);

        if (take2Res.getType() == HitResult.Type.BLOCK && take2Res instanceof BlockHitResult) {

            BlockPos pos = ((BlockHitResult) take2Res).getBlockPos().below();
            BlockState state = world.getBlockState(pos);

            if (player.position().y - pos.getY() > 1 && (world.isEmptyBlock(pos) || state.getMaterial().isReplaceable()))
                return new Tuple<>(pos, Direction.DOWN);
        }

        return null;
    }

    private static Tuple<BlockPos, Direction> getHorizontalTarget(Player player, Level world, Vec3 rayPos, Vec3 ray) {
        Direction dir = Direction.fromYRot(player.yHeadRot);

        Vec3 newPos = rayPos.add(new Vec3(-(leniency * dir.getStepX()), 0, -(leniency * dir.getStepZ())));
        HitResult take2Res = RayTraceHandler.rayTrace(player, world, newPos, newPos.add(ray), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);

        if (take2Res.getType() == HitResult.Type.BLOCK && take2Res instanceof BlockHitResult) {
            BlockPos pos = ((BlockHitResult) take2Res).getBlockPos().relative(dir);
            BlockState state = world.getBlockState(pos);

            if ((world.isEmptyBlock(pos) || state.getMaterial().isReplaceable())) return new Tuple<>(pos, dir.getOpposite());
        }

        return null;
    }

    private static boolean isSupportedStack(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BlockItem;
    }

    public static boolean isInVerticalOrientation() {
        return ReacharoundTracker.verticalOrientation;
    }
}
