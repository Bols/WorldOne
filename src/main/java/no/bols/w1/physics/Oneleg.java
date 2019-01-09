package no.bols.w1.physics;//

public class Oneleg extends PhysObject {

    private static float MAX_SPEED = 1; //positions pr. second
    private static float EAT_SPEED = 1; //amount pr second
    private Brain brain;
    private long lastPositionTime = 0;
    private float lastMotorOutput;

    public Oneleg(World world, Brain brain, float position) {
        super(position, world);
        this.brain = brain;
        brain.setOneleg(this);
    }

    public float getFoodDistanceOutput() {
        updateState();
        return distance(world.getCurrentFood());
    }

    public void motorOutput(float output) {
        if (output > 1.0) {
            throw new RuntimeException("Motor output=" + output);
        }
        updateState();
        this.lastMotorOutput = output;

    }


    private void updateState() {
        long newTimeMs = world.getTime().getTimeMicroSeconds();
        long diffTimeMs = newTimeMs - lastPositionTime;
        position = position + (lastMotorOutput * MAX_SPEED * diffTimeMs / 1000000);
        if (canEat()) {
            world.getCurrentFood().eat(EAT_SPEED * diffTimeMs / 1000000);
        }

        lastPositionTime = newTimeMs;
    }

    private boolean canEat() {
        return distance(world.getCurrentFood()) < .1 && world.getCurrentFood().getFoodAmount() > 0;
    }


}
