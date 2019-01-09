package no.bols.w1.physics;//

public class Oneleg extends PhysObject {

    private static double MAX_SPEED = 1; //positions pr. second
    private static double EAT_SPEED = 1; //amount pr second
    private Brain brain;
    private long lastPositionTime = 0;
    private double lastMotorOutput;

    public Oneleg(World world, Brain brain, double position) {
        super(position, world);
        this.brain = brain;
        brain.setOneleg(this);
    }

    public double getFoodDistanceOutput() {
        updateState();
        return distance(world.getCurrentFood());
    }

    public void motorOutput(double output) {
        if (output > 1.0) {
            throw new RuntimeException("Motor output=" + output);
        }
        updateState();
        this.lastMotorOutput = output;

    }


    private void updateState() {
        long newTimeMs = world.getTime().getTimeMilliSeconds();
        long diffTimeMs = newTimeMs - lastPositionTime;
        position = position + (lastMotorOutput * MAX_SPEED * diffTimeMs / 1000);
        if (canEat()) {
            world.getCurrentFood().eat(EAT_SPEED * diffTimeMs / 1000);
        }

        lastPositionTime = newTimeMs;
    }

    private boolean canEat() {
        return distance(world.getCurrentFood()) < .1 && world.getCurrentFood().getFoodAmount() > 0;
    }


}
