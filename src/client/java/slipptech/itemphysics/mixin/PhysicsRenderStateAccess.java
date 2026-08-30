package slipptech.itemphysics.mixin;

public interface PhysicsRenderStateAccess {

    void itemPhysics$setPitch(float value);

    void itemPhysics$setYaw(float value);

    void itemPhysics$setRoll(float value);

    float itemPhysics$getPitch();

    float itemPhysics$getYaw();

    float itemPhysics$getRoll();
}