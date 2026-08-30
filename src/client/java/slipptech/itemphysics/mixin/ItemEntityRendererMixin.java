package slipptech.itemphysics.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slipptech.itemphysics.physics.PhysicsEngine;
import slipptech.itemphysics.physics.PhysicsState;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    /*
     * Before rendering, copy the physics state from
     * the real ItemEntity into the temporary render state.
     */
    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void itemPhysics$extract(
            ItemEntity entity,
            ItemEntityRenderState state,
            float partialTicks,
            CallbackInfo ci
    ) {

        PhysicsState physics =
                PhysicsEngine.getState(entity);

        PhysicsRenderStateAccess access =
                (PhysicsRenderStateAccess)
                        (Object) state;

        access.itemPhysics$setPitch(
                physics.getPitch()
        );

        access.itemPhysics$setYaw(
                physics.getYaw()
        );

        access.itemPhysics$setRoll(
                physics.getRoll()
        );
    }

    /*
     * Replace vanilla floating/spinning with our physical
     * rotation.
     */
    @Inject(
            method = "submit",
            at = @At("HEAD"),
            cancellable = true
    )
    private void itemPhysics$submit(
            ItemEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera,
            CallbackInfo ci
    ) {

        if (state.item.isEmpty()) {
            return;
        }

        PhysicsRenderStateAccess physicsState =
                (PhysicsRenderStateAccess)
                        (Object) state;

        poseStack.pushPose();

        AABB boundingBox =
                state.item.getModelBoundingBox();

        float minY =
                (float) boundingBox.minY;

        float maxY =
                (float) boundingBox.maxY;

        float centerY =
                (minY + maxY) * 0.5F;

        /*
         * Put the bottom of the model onto the surface.
         */
        float surfaceOffset =
                -minY + 0.018F;

        poseStack.translate(
                0.0F,
                surfaceOffset,
                0.0F
        );

        /*
         * Rotate around the approximate center of the model.
         *
         * This is much better than rotating around the
         * bottom edge of the model.
         */
        poseStack.translate(
                0.0F,
                centerY,
                0.0F
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        physicsState.itemPhysics$getYaw()
                )
        );

        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        physicsState.itemPhysics$getPitch()
                )
        );

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        physicsState.itemPhysics$getRoll()
                )
        );

        poseStack.translate(
                0.0F,
                -centerY,
                0.0F
        );

        /*
         * Existing Minecraft stack renderer.
         *
         * We keep this instead of manually rendering every
         * stack member.
         */
        ItemEntityRenderer.submitMultipleFromCount(
                poseStack,
                collector,
                state.lightCoords,
                state,
                net.minecraft.util.RandomSource.create(
                        state.seed
                ),
                boundingBox
        );

        poseStack.popPose();

        /*
         * Prevent vanilla bob + spin.
         */
        ci.cancel();
    }
}