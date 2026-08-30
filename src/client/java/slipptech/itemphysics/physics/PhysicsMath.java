package slipptech.itemphysics.physics;

public final class PhysicsMath {

    private PhysicsMath() {
    }

    public static float approach(
            float current,
            float target,
            float amount
    ) {
        if (current < target) {
            return Math.min(current + amount, target);
        }

        if (current > target) {
            return Math.max(current - amount, target);
        }

        return target;
    }

    public static float damp(
            float value,
            float factor
    ) {
        return value * factor;
    }

    public static double damp(
            double value,
            double factor
    ) {
        return value * factor;
    }

    public static float clamp(
            float value,
            float min,
            float max
    ) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(min, Math.min(max, value));
    }

    public static float normalizeAngle(float angle) {
        angle %= 360.0F;

        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }

    public static float angleDifference(
            float target,
            float current
    ) {
        return normalizeAngle(target - current);
    }
}