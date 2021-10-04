package me.cg360.mod.placement.raytrace;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Objects;

public class RayTraceHandler {

    public static HitResult rayTrace(Entity entity, World world, PlayerEntity player, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode) {
        return rayTrace(entity, world, player, blockMode, fluidMode, getEntityRange(player));
    }

    public static HitResult rayTrace(Entity entity, World world, Entity player, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode, double range) {
        Pair<Vec3d, Vec3d> params = getEntityParams(player);
        return rayTrace(entity, world, params.getLeft(), params.getRight(), blockMode, fluidMode, range);
    }

    public static HitResult rayTrace(Entity entity, World world, Vec3d startPos, Vec3d ray, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode, double range) {
        return rayTrace(entity, world, startPos, ray.multiply(range), blockMode, fluidMode);
    }

    public static HitResult rayTrace(Entity entity, World world, Vec3d startPos, Vec3d ray, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode) {
        Vec3d end = startPos.add(ray);
        RaycastContext context = new RaycastContext(startPos, end, blockMode, fluidMode, entity);
        return world.raycast(context);
    }

    public static double getEntityRange(PlayerEntity player) {
        return 5d; //TODO: Check if the player is in survival or creative
    }

    /** *
     *
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
