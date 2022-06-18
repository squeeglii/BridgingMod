package me.cg360.mod.placement.raytrace;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RayTraceHandler {

    public static HitResult rayTrace(Entity entity, World world, PlayerEntity player, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode) {
        return rayTrace(entity, world, player, blockMode, fluidMode, getEntityRange(player));
    }

    public static HitResult rayTrace(Entity entity, World world, Entity player, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode, double range) {
        Pair<Vec3d, Vec3d> params = getEntityParams(player);
        return rayTrace(entity, world, params.getLeft(), params.getRight(), blockMode, fluidMode, range);
    }

    public static HitResult rayTrace(Entity entity, World world, Vec3d startPos, Vec3d ray, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode, double range) {
        return rayTrace(entity, world, startPos, startPos.add(ray.multiply(range)), blockMode, fluidMode);
    }

    public static HitResult rayTrace(Entity entity, World world, Vec3d startPos, Vec3d endPos, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode) {
        RaycastContext context = new RaycastContext(startPos, endPos, blockMode, fluidMode, entity);

        return world.raycast(context);
    }

    /**
     * Gets the maximum place distance for a given player.
     * @param player the player to check
     * @return the distance at which the player can place.
     */
    public static double getEntityRange(PlayerEntity player) {
        if(player.getWorld().isClient) {
            MinecraftClient cli = MinecraftClient.getInstance();
            ClientPlayerInteractionManager interact = cli.interactionManager;
            if (interact != null)
                return interact.getReachDistance();
        }
        return 4.5d;
    }

    /**
     * @param player - the player entity using the raycast guide.
     * @return Pair | Left = Starting position, Right = Direction
     */
    public static Pair<Vec3d, Vec3d> getEntityParams(Entity player) {
        float pitch = player.prevPitch + (player.getPitch() - player.prevPitch);
        float yaw = player.prevYaw + (player.getYaw() - player.prevYaw);
        Vec3d pos = player.getPos();
        double posX = player.prevX + (pos.x - player.prevX);
        double posY = player.prevY + (pos.y - player.prevY);
        if (player instanceof PlayerEntity) posY += player.getEyeHeight(player.getPose());
        double posZ = player.prevZ + (pos.z - player.prevZ);
        Vec3d rayPos = new Vec3d(posX, posY, posZ);

        float zYaw = -MathHelper.cos(yaw * (float) Math.PI / 180);
        float xYaw = MathHelper.sin(yaw * (float) Math.PI / 180);
        float pitchMod = -MathHelper.cos(pitch * (float) Math.PI / 180);
        float azimuth = -MathHelper.sin(pitch * (float) Math.PI / 180);
        float xLen = xYaw * pitchMod;
        float yLen = zYaw * pitchMod;
        Vec3d ray = new Vec3d(xLen, azimuth, yLen);

        return new Pair<>(rayPos, ray);
    }


}
