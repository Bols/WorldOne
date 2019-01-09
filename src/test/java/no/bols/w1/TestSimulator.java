package no.bols.w1;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterValue;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.SortedMap;

/**
 * Unit test for simple App.
 */
public class TestSimulator
        extends TestCase {
    public static final String MOVESPEEDPARAM = "moveparam";
    public static final String STOPDISTANCEPARAM = "stopdistance";

    public TestSimulator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestSimulator.class);
    }

    public void testGeneSimulation() {
        World1SimulatorApplication simulator = World1SimulatorApplication.builder().scenarioTimeMs(100000000).build();
        SortedMap<Double, GeneMap> result = simulator.runAnalysisUntilStable(new TestBrainFactory());
        GeneParameterValue bestTestParam = (GeneParameterValue) result.get(result.firstKey()).genes.get(MOVESPEEDPARAM);
        assertEquals(.5f, bestTestParam.getValue(), .01);
    }


    private class TestGenedBrain extends Brain {
        private final float moveParam;
        private final float stopDistanceParam;

        public TestGenedBrain(Time time, GeneMap geneMap) {
            super(time);
            moveParam = ((GeneParameterValue) geneMap.genes.get(MOVESPEEDPARAM)).getValue();
            stopDistanceParam = ((GeneParameterValue) geneMap.genes.get(STOPDISTANCEPARAM)).getValue();
            time.scheduleRecurringEvent(t -> moveUntilNextToFood(t), 10000);
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodDistanceOutput() > stopDistanceParam) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf());
            } else {
                oneleg.motorOutput(0.0f);
            }
        }

        private float calculateOutputWithMaxValueAchievedAtMoveParamHalf() {
            if (moveParam < .3 || moveParam > .7) {
                return 0;
            }
            return Math.max(0, 1 - Math.abs(moveParam - .5f));
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

            return geneMap;
        }
    }
}
