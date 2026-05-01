package me.cg360.mod.bridging.raytrace;

import me.cg360.mod.bridging.BridgingMod;
import me.cg360.mod.bridging.config.selector.SourcePerspective;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class Perspective {

    private Supplier<Vec3> pos;
    private Supplier<Vector3f> lookVector;

    public Perspective(Supplier<Vec3> pos, Supplier<Vector3f> lookVector) {
        this.pos = pos;
        this.lookVector = lookVector;
    }


    public Vec3 getPosition() {
        return this.pos.get();
    }

    public Vector3f getLookVector() {
        return this.lookVector.get();
    }

    public static Perspective fromCamera(Camera camera) {
        return new Perspective(camera::getPosition, camera::getLookVector);
    }

    public static Perspective fromEntity(Entity entity, float partialTicks) {
        return new Perspective(() -> entity.getEyePosition(partialTicks), () -> entity.getViewVector(partialTicks).toVector3f());
    }

    public static Perspective fromEntity(Entity entity) {
        return new Perspective(entity::getEyePosition, () -> entity.getViewVector(0f).toVector3f());
    }



    public static Perspective getSourcePerspective(Player player, float partialTicks) {
        SourcePerspective perspectiveLock = BridgingMod.getCompatibleSourcePerspective();

        return switch (perspectiveLock) {
            case COPY_TOGGLE_PERSPECTIVE, LET_BRIDGING_MOD_DECIDE ->
                    Perspective.fromCamera(Minecraft.getInstance().gameRenderer.getMainCamera());

            case ALWAYS_EYELINE ->
                    Perspective.fromEntity(player, partialTicks);
        };
    }

    // Mod uses the previous tick and seems to work just fine. Sable compatiblity specified
    // the partial ticks to stop contraption desync.
    public static Perspective getSourcePerspective(Player player) {
        return Perspective.getSourcePerspective(player, 0f);
    }
}
