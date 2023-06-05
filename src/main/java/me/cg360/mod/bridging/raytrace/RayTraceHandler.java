package me.cg360.mod.bridging.raytrace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceHandler {

    public static HitResult rayTrace(Entity entity, Level world, Vec3 startPos, Vec3 endPos, ClipContext.Block blockMode, ClipContext.Fluid fluidMode) {
        ClipContext context = new ClipContext(startPos, endPos, blockMode, fluidMode, entity);
        return world.clip(context);
    }

    /**
     * Gets the maximum place distance for a given player.
     * @param player the player to check
     * @return the distance at which the player can place.
     */
    public static double getEntityRange(Player player) {
        if(!player.level().isClientSide) {
            Minecraft cli = Minecraft.getInstance();
            MultiPlayerGameMode interact = cli.gameMode;

            if (interact != null)
                return interact.getPickRange();
        }

        return 4.5d;
    }

    /**
     * @param player - the player entity using the raycast guide.
     * @return Pair | Left = Starting position, Right = Direction
     */
    public static Tuple<Vec3, Vec3> getEntityParams(Entity player) {
        float pitch = player.xRotO + (player.getXRot() - player.xRotO);
        float yaw = player.yRotO + (player.getYRot() - player.yRotO);
        Vec3 pos = player.position();

        double posX = player.xo + (pos.x - player.xo);
        double posY = player.yo + (pos.y - player.yo);
        if (player instanceof Player)
            posY += player.getEyeHeight(player.getPose());

        double posZ = player.zo + (pos.z - player.zo);
        Vec3 rayPos = new Vec3(posX, posY, posZ);

        float zYaw = -Mth.cos(yaw * (float) Math.PI / 180);
        float xYaw = Mth.sin(yaw * (float) Math.PI / 180);
        float pitchMod = -Mth.cos(pitch * (float) Math.PI / 180);
        float azimuth = -Mth.sin(pitch * (float) Math.PI / 180);
        float xLen = xYaw * pitchMod;
        float yLen = zYaw * pitchMod;
        Vec3 ray = new Vec3(xLen, azimuth, yLen);

        return new Tuple<>(rayPos, ray);
    }


}
