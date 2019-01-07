package no.bols.w1;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TestPhysics
        extends TestCase {
    public TestPhysics(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPhysics.class);
    }

    public void testPhysics() {
        Time time = new Time();
        World world = new World(time, new SteadySpeedBrain(time));
        time.runUntil(t -> world.getCurrentFood().eaten());

    }


    private class SteadySpeedBrain extends Brain {
        public SteadySpeedBrain(Time time) {
            super(time);
            time.scheduleRecurringEvent(t -> moveUntilNextToFood(t), 10);
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodDistanceOutput() > .1) {
                oneleg.motorOutput(1.0f);
            } else {
                oneleg.motorOutput(0.0f);
            }
        }


    }
}
