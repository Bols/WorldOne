package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterValue;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.SortedSet;

/**
 * Unit test for simple App.
 */
public class TestSimulator
        extends TestCase {
    public static final String MOVESPEEDPARAM = "moveparam";
    public static final String STOPDISTANCEPARAM = "stopdistance";
    private static final String STOPSPEEDPARAM = "stopspeed";


    public TestSimulator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestSimulator.class);
    }

    public void testGeneSimulation() {
        World1SimulatorRunner simulator = World1SimulatorRunner.builder()
                .scenarioTimeMs(25000).build();
        SortedSet<Pair<Double, GeneMap>> result = simulator.runGeneticAlgorithmUntilStable(new TestBrainFactory());
        GeneParameterValue bestTestParam = (GeneParameterValue) result.first().getValue().genes.get(MOVESPEEDPARAM);
        assertEquals(.5f, bestTestParam.getValue(), .05);
    }


    private class TestGenedBrain extends Brain {
        private final double moveParam;
        private final double stopDistanceParam;
        private final double stopSpeedParam;

        public TestGenedBrain(Time time, GeneMap geneMap) {
            super(time);
            moveParam = ((GeneParameterValue) geneMap.genes.get(MOVESPEEDPARAM)).getValue();
            stopDistanceParam = ((GeneParameterValue) geneMap.genes.get(STOPDISTANCEPARAM)).getValue();
            stopSpeedParam = ((GeneParameterValue) geneMap.genes.get(STOPSPEEDPARAM)).getValue();

        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodDistanceOutput() > stopDistanceParam) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf());
            } else {
                oneleg.motorOutput(stopSpeedParam);
            }
        }

        private double calculateOutputWithMaxValueAchievedAtMoveParamHalf() {
            if (moveParam < .3 || moveParam > .7) {
                return 0;
            }
            return Math.max(0, 1 - Math.abs(moveParam - .5));
        }

        @Override
        public void initializeRecurringInputEvents() {
            time.scheduleRecurringEvent(t -> moveUntilNextToFood(t), 10);
        }
    }

    private class TestBrainFactory implements BrainFactory {



        @Override
        public Brain createBrain(Time time, GeneMap genes) {
            return new TestGenedBrain(time, genes);
        }

        @Override
        public GeneMap randomGenes() {
            GeneMap geneMap = new GeneMap();
            geneMap.genes.put(MOVESPEEDPARAM, new GeneParameterValue(0, 1));
            geneMap.genes.put(STOPDISTANCEPARAM, new GeneParameterValue(0, 1));
            geneMap.genes.put(STOPSPEEDPARAM, new GeneParameterValue(0, 1));

            return geneMap;
        }
    }
}