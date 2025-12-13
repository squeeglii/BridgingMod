package me.cg360.mod.bridging.raytrace;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

import java.util.function.Supplier;

public class Perspective {

    private Supplier<Vec3> pos;
    private Supplier<Vector3fc> lookVector;

    public Perspective(Supplier<Vec3> pos, Supplier<Vector3fc> lookVector) {
        this.pos = pos;
        this.lookVector = lookVector;
    }


    public Vec3 getPosition() {
        return this.pos.get();
    }

    public Vector3fc getLookVector() {
        return this.lookVector.get();
    }

    public static Perspective fromCamera(Camera camera) {
        return new Perspective(camera::position, camera::forwardVector);
    }

    public static Perspective fromEntity(Entity entity) {
        return new Perspective(entity::getEyePosition, () -> entity.getViewVector(0f).toVector3f());
    }

}
