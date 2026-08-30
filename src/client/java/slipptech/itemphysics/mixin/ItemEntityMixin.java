package slipptech.itemphysics.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slipptech.itemphysics.physics.PhysicsEngine;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void itemPhysics$tick(
            CallbackInfo ci
    ) {
        ItemEntity entity =
                (ItemEntity) (Object) this;

        PhysicsEngine.tick(entity);
    }
}