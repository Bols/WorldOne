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
    public static final String TESTPARAM = "testparam";

    public TestSimulator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestSimulator.class);
    }

    public void testGeneSimulation() {
        SortedMap<Double, GeneMap> result = new World1SimulatorApplication().runAnalysisUntilStable(new TestBrainFactory());
        GeneParameterValue bestTestParam = (GeneParameterValue) result.get(result.firstKey()).genes.get(TESTPARAM);
        assertEquals(.5f, bestTestParam.getValue(), .001);
    }


    private class TestGenedBrain extends Brain {
        private final float testParam;

        public TestGenedBrain(Time time, GeneMap geneMap) {
            super(time);
            testParam = ((GeneParameterValue) geneMap.genes.get(TESTPARAM)).getValue();
            time.scheduleRecurringEvent(t -> moveUntilNextToFood(t), 10);
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodDistanceOutput() > .1) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtTestParamHalf());
            } else {
                oneleg.motorOutput(0.0f);
            }
        }

        private float calculateOutputWithMaxValueAchievedAtTestParamHalf() {
          /*  if(testParam<.3 || testParam>.7){
                return 0;
            }*/
            return Math.max(0, 1 - Math.abs(testParam - .5f));
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
            geneMap.genes.put(TESTPARAM, new GeneParameterValue(0, 1));
            return geneMap;
        }
    }
}
