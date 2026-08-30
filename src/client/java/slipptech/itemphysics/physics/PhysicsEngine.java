package slipptech.itemphysics.physics;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Map;
import java.util.WeakHashMap;

public final class PhysicsEngine {

    private static final Map<ItemEntity, PhysicsState> STATES =
            new WeakHashMap<>();

    private PhysicsEngine() {
    }

    public static PhysicsState getState(ItemEntity entity) {
        PhysicsState state = STATES.get(entity);

        if (state == null) {
            state = new PhysicsState();

            RandomSource random =
                    RandomSource.create(
                            entity.getId() * 92821L
                                    + entity.getItem().hashCode()
                    );

            float yaw =
                    random.nextFloat() * 360.0F;

            state.initialize(yaw);

            STATES.put(entity, state);
        }

        return state;
    }

    public static void tick(ItemEntity entity) {

        if (entity.isRemoved()) {
            STATES.remove(entity);
            return;
        }

        PhysicsState state = getState(entity);

        double vx = entity.getDeltaMovement().x;
        double vy = entity.getDeltaMovement().y;
        double vz = entity.getDeltaMovement().z;

        state.setPreviousVelocity(
                state.getVelocityX(),
                state.getVelocityY(),
                state.getVelocityZ()
        );

        state.setVelocity(vx, vy, vz);

        boolean grounded = entity.onGround();

        state.setWasGrounded(
                state.isGrounded()
        );

        state.setGrounded(grounded);

        state.tickCooldown();

        /*
         * Detect vertical impact.
         *
         * The real ItemEntity already collided with the world.
         * We use that collision to generate visual rotation.
         */
        if (
                grounded
                        && !state.wasGrounded()
                        && state.getPreviousVelocityY() < -0.08
        ) {
            double impactSpeed =
                    Math.abs(state.getPreviousVelocityY());

            onImpact(
                    entity,
                    state,
                    impactSpeed,
                    0.0,
                    1.0,
                    0.0
            );
        }

        /*
         * Detect a strong horizontal velocity change.
         *
         * This catches many wall / edge collisions.
         */
        double horizontalDeltaX =
                vx - state.getPreviousVelocityX();

        double horizontalDeltaZ =
                vz - state.getPreviousVelocityZ();

        double horizontalImpact =
                Math.sqrt(
                        horizontalDeltaX * horizontalDeltaX
                                + horizontalDeltaZ * horizontalDeltaZ
                );

        if (
                horizontalImpact > 0.045
                        && horizontalImpact < 2.0
                        && state.getImpactCooldown() == 0
        ) {
            /*
             * Only react strongly if the item was actually moving.
             */
            double previousHorizontal =
                    Math.sqrt(
                            state.getPreviousVelocityX()
                                    * state.getPreviousVelocityX()
                                    +
                                    state.getPreviousVelocityZ()
                                            * state.getPreviousVelocityZ()
                    );

            if (previousHorizontal > 0.08) {

                double normalX =
                        -horizontalDeltaX;

                double normalZ =
                        -horizontalDeltaZ;

                double length =
                        Math.sqrt(
                                normalX * normalX
                                        + normalZ * normalZ
                        );

                if (length > 0.0001) {
                    normalX /= length;
                    normalZ /= length;

                    onImpact(
                            entity,
                            state,
                            horizontalImpact,
                            normalX,
                            0.0,
                            normalZ
                    );
                }
            }
        }

        /*
         * While airborne the object keeps tumbling.
         */
        if (!grounded) {

            /*
             * Slight aerodynamic damping.
             */
            state.dampAngularVelocity(0.992F);

            state.resetSettleTicks();

        } else {

            /*
             * Ground friction.
             */
            state.dampAngularVelocity(0.78F);

            state.incrementSettleTicks();

            /*
             * Once the object has almost stopped rotating,
             * smoothly settle it onto the ground.
             */
            if (
                    Math.abs(state.getAngularVelocityX()) < 0.05F
                            && Math.abs(state.getAngularVelocityZ()) < 0.05F
            ) {
                float settle =
                        Mth.clamp(
                                state.getSettleTicks() / 18.0F,
                                0.0F,
                                1.0F
                        );

                state.addAngularVelocity(
                        -state.getPitch()
                                * 0.0015F
                                * settle,

                        0.0F,

                        -state.getRoll()
                                * 0.0015F
                                * settle
                );
            }
        }

        state.updateRotation();
    }

    private static void onImpact(
            ItemEntity entity,
            PhysicsState state,
            double impactSpeed,
            double normalX,
            double normalY,
            double normalZ
    ) {

        if (state.getImpactCooldown() > 0) {
            return;
        }

        PhysicsMaterial material =
                getMaterial(entity);

        /*
         * Very weak impacts should not make the object
         * spin violently.
         */
        float impact =
                (float) Mth.clamp(
                        impactSpeed,
                        0.0,
                        1.5
                );

        if (impact < 0.06F) {
            return;
        }

        /*
         * Main rotation impulse.
         *
         * Collision normal determines which axes receive
         * the rotational impulse.
         */
        float impulse =
                impact
                        * (1.0F / material.mass())
                        * 7.5F;

        float torqueX =
                (float) (normalZ * impulse);

        float torqueZ =
                (float) (-normalX * impulse);

        /*
         * A vertical impact also creates some natural
         * sideways rotation based on the item's movement.
         */
        double vx = state.getVelocityX();
        double vz = state.getVelocityZ();

        float movementTorque =
                (float)
                        Math.min(
                                1.0,
                                Math.sqrt(
                                        vx * vx + vz * vz
                                )
                        )
                        * 2.5F;

        torqueX +=
                (float) (-vz * movementTorque);

        torqueZ +=
                (float) (vx * movementTorque);

        state.addAngularVelocity(
                torqueX,
                (float) (impulse * 0.12F),
                torqueZ
        );

        /*
         * Bounce visual impulse.
         */
        float bounce =
                impact
                        * material.restitution()
                        * 0.16F;

        if (bounce > 0.025F) {
            state.setBounceVelocity(
                    Math.min(
                            bounce,
                            0.18F
                    )
            );
        }

        state.setImpactCooldown(2);
    }

    private static PhysicsMaterial getMaterial(
            ItemEntity entity
    ) {

        /*
         * We deliberately keep this lightweight.
         *
         * Later this can be replaced with a proper
         * configurable material registry.
         */
        String id =
                entity.getItem()
                        .getItem()
                        .toString()
                        .toLowerCase();

        if (
                id.contains("feather")
                        || id.contains("snowball")
        ) {
            return PhysicsMaterial.LIGHT;
        }

        if (
                id.contains("anvil")
                        || id.contains("heavy")
        ) {
            return PhysicsMaterial.HEAVY;
        }

        if (
                id.contains("slime")
                        || id.contains("snow")
        ) {
            return PhysicsMaterial.BOUNCY;
        }

        return PhysicsMaterial.DEFAULT;
    }
}