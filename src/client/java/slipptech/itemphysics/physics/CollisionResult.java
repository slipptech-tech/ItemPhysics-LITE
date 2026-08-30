package slipptech.itemphysics.physics;

public record CollisionResult(
        boolean collision,
        double normalX,
        double normalY,
        double normalZ,
        double impactSpeed
) {

    public static CollisionResult none() {
        return new CollisionResult(
                false,
                0.0,
                0.0,
                0.0,
                0.0
        );
    }

    public static CollisionResult ground(double impactSpeed) {
        return new CollisionResult(
                true,
                0.0,
                1.0,
                0.0,
                impactSpeed
        );
    }

    public static CollisionResult wall(
            double normalX,
            double normalY,
            double normalZ,
            double impactSpeed
    ) {
        return new CollisionResult(
                true,
                normalX,
                normalY,
                normalZ,
                impactSpeed
        );
    }
}