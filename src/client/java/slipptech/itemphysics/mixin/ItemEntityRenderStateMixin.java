package slipptech.itemphysics.mixin;

import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import slipptech.itemphysics.physics.PhysicsEngine;
import slipptech.itemphysics.physics.PhysicsState;

@Mixin(ItemEntityRenderState.class)
public abstract class ItemEntityRenderStateMixin
        implements PhysicsRenderStateAccess {

    private float itemPhysics$pitch;
    private float itemPhysics$yaw;
    private float itemPhysics$roll;

    @Override
    public void itemPhysics$setPitch(float value) {
        this.itemPhysics$pitch = value;
    }

    @Override
    public void itemPhysics$setYaw(float value) {
        this.itemPhysics$yaw = value;
    }

    @Override
    public void itemPhysics$setRoll(float value) {
        this.itemPhysics$roll = value;
    }

    @Override
    public float itemPhysics$getPitch() {
        return itemPhysics$pitch;
    }

    @Override
    public float itemPhysics$getYaw() {
        return itemPhysics$yaw;
    }

    @Override
    public float itemPhysics$getRoll() {
        return itemPhysics$roll;
    }
}