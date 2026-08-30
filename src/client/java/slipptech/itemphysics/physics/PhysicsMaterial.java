package slipptech.itemphysics.physics;

public record PhysicsMaterial(
        float mass,
        float restitution,
        float friction,
        float angularDamping
) {

    public static final PhysicsMaterial DEFAULT =
            new PhysicsMaterial(
                    1.0F,
                    0.22F,
                    0.78F,
                    0.82F
            );

    public static final PhysicsMaterial LIGHT =
            new PhysicsMaterial(
                    0.35F,
                    0.38F,
                    0.55F,
                    0.70F
            );

    public static final PhysicsMaterial HEAVY =
            new PhysicsMaterial(
                    1.75F,
                    0.12F,
                    0.92F,
                    0.90F
            );

    public static final PhysicsMaterial BOUNCY =
            new PhysicsMaterial(
                    0.75F,
                    0.45F,
                    0.48F,
                    0.68F
            );
}