package no.bols.w1.physics;//

import lombok.Getter;

public class Oneleg extends PhysObject {

    private static double MAX_SPEED = 1; //positions pr. second
    private static double EAT_SPEED = .5; //amount pr second
    private Brain brain;
    private Time.Instant lastPositionTime;
    @Getter
    private double lastMotorOutput;

    public Oneleg(World world, Brain brain, double position) {
        super(position, world);
        this.brain = brain;
        brain.setOneleg(this);
    }

    public double getFoodProximityOutput() {
        updateState();
        return 1.0 / (Math.pow(1.2, distance(world.getCurrentFood())));
    }

    public double getEatingOutput() {
        updateState();
        return canEat() ? 1 : 0;
    }

    public void motorOutput(double output) {
        if (output > 1.0) {
            throw new RuntimeException("Motor output=" + output);
        }
        updateState();
        this.lastMotorOutput = output;

    }


    private void updateState() {
        Time.Instant now = world.getTime().getSimulatedTime();
        long diffTimeMs = now.timeSince(lastPositionTime);
        position = position + (lastMotorOutput * MAX_SPEED * diffTimeMs / 1000);
        if (canEat()) {
            world.getCurrentFood().eat(EAT_SPEED * diffTimeMs / 1000);
        }

        lastPositionTime = now;
    }

    private boolean canEat() {
        return lastMotorOutput < .01 && distance(world.getCurrentFood()) < .1 && world.getCurrentFood().getFoodAmount() > 0;
    }


}
