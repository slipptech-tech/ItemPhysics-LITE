package slipptech.itemphysics.physics;

public final class PhysicsState {

    /*
     * Поворот предмета.
     */
    private float pitch;
    private float yaw;
    private float roll;

    /*
     * Угловая скорость.
     */
    private float angularVelocityX;
    private float angularVelocityY;
    private float angularVelocityZ;

    /*
     * Предыдущая скорость ItemEntity.
     */
    private double previousVelocityX;
    private double previousVelocityY;
    private double previousVelocityZ;

    /*
     * Последняя известная скорость.
     */
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    /*
     * Состояние поверхности.
     */
    private boolean grounded;
    private boolean wasGrounded;

    /*
     * Сила текущего bounce.
     */
    private float bounceVelocity;

    /*
     * Сколько тиков предмет уже стабилизируется.
     */
    private int settleTicks;

    /*
     * Первоначальная инициализация.
     */
    private boolean initialized;

    /*
     * Стабильный случайный yaw.
     */
    private float baseYaw;

    /*
     * Защита от слишком частого сильного bounce.
     */
    private int impactCooldown;

    public void initialize(float yaw) {
        if (initialized) {
            return;
        }

        this.baseYaw = yaw;
        this.yaw = yaw;

        this.pitch = 0.0F;
        this.roll = 0.0F;

        this.initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public float getAngularVelocityX() {
        return angularVelocityX;
    }

    public float getAngularVelocityY() {
        return angularVelocityY;
    }

    public float getAngularVelocityZ() {
        return angularVelocityZ;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean wasGrounded() {
        return wasGrounded;
    }

    public float getBounceVelocity() {
        return bounceVelocity;
    }

    public int getSettleTicks() {
        return settleTicks;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public void setWasGrounded(boolean wasGrounded) {
        this.wasGrounded = wasGrounded;
    }

    public void setVelocity(
            double x,
            double y,
            double z
    ) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
    }

    public void setPreviousVelocity(
            double x,
            double y,
            double z
    ) {
        this.previousVelocityX = x;
        this.previousVelocityY = y;
        this.previousVelocityZ = z;
    }

    public double getPreviousVelocityY() {
        return previousVelocityY;
    }

    public double getPreviousVelocityX() {
        return previousVelocityX;
    }

    public double getPreviousVelocityZ() {
        return previousVelocityZ;
    }

    public void addAngularVelocity(
            float x,
            float y,
            float z
    ) {
        angularVelocityX += x;
        angularVelocityY += y;
        angularVelocityZ += z;

        angularVelocityX =
                PhysicsMath.clamp(
                        angularVelocityX,
                        -35.0F,
                        35.0F
                );

        angularVelocityY =
                PhysicsMath.clamp(
                        angularVelocityY,
                        -35.0F,
                        35.0F
                );

        angularVelocityZ =
                PhysicsMath.clamp(
                        angularVelocityZ,
                        -35.0F,
                        35.0F
                );
    }

    public void updateRotation() {
        pitch += angularVelocityX;
        yaw += angularVelocityY;
        roll += angularVelocityZ;

        pitch = PhysicsMath.normalizeAngle(pitch);
        yaw = PhysicsMath.normalizeAngle(yaw);
        roll = PhysicsMath.normalizeAngle(roll);
    }

    public void dampAngularVelocity(float amount) {
        angularVelocityX *= amount;
        angularVelocityY *= amount;
        angularVelocityZ *= amount;
    }

    public void setBounceVelocity(float value) {
        bounceVelocity = value;
    }

    public void dampBounce(float amount) {
        bounceVelocity *= amount;
    }

    public void incrementSettleTicks() {
        settleTicks++;
    }

    public void resetSettleTicks() {
        settleTicks = 0;
    }

    public int getImpactCooldown() {
        return impactCooldown;
    }

    public void setImpactCooldown(int value) {
        impactCooldown = Math.max(0, value);
    }

    public void tickCooldown() {
        if (impactCooldown > 0) {
            impactCooldown--;
        }
    }

    public float getBaseYaw() {
        return baseYaw;
    }
}