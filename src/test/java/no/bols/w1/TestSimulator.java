package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.genes.*;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * Unit test for simple App.
 */
public class TestSimulator
        extends TestCase {
    public static final String MOVESPEEDPARAM = "moveparam";
    public static final String STOPDISTANCEPARAM = "stopdistance";
    private static final String STOPSPEEDPARAM = "stopspeed";
    public static final int TUNE_FACTOR_NUM = 10;
    public static final String TUNE_GENE_NAME = "Gene";


    public TestSimulator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestSimulator.class);
    }

    public void testGeneSimulation() {
        World1SimulatorRunner simulator = World1SimulatorRunner.builder()
                .scenarioTimeMs(25000)
                .brainFactory(new TestBrainFactory())
                .build();
        SortedSet<Pair<Double, GeneMap>> result = simulator.runGeneticAlgorithmUntilStable();
        Map<String, Gene> paramMap = result.first().getValue().genes;
        for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
            assertEquals(.5f, ((GeneParameterValue) paramMap.get(TUNE_GENE_NAME + i)).getValue(), .05);
        }
    }


    private class TestGenedBrain extends Brain {
        private final double stopDistanceParam;
        private final double stopSpeedParam;
        private final GeneMap geneMap;

        public TestGenedBrain(Time time, GeneMap geneMap) {
            super(time);
            stopDistanceParam = ((GeneParameterValue) geneMap.genes.get(STOPDISTANCEPARAM)).getValue();
            stopSpeedParam = ((GeneParameterValue) geneMap.genes.get(STOPSPEEDPARAM)).getValue();
            this.geneMap = geneMap;
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodProximityOutput() < stopDistanceParam) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf());
            } else {
                oneleg.motorOutput(stopSpeedParam);
            }
        }

        private double calculateOutputWithMaxValueAchievedAtMoveParamHalf() {
            double moveFactor = 1;
            for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
                double gene_i = ((GeneParameterValue) geneMap.genes.get(TUNE_GENE_NAME + i)).getValue();
                moveFactor = moveFactor * Math.max(0, 1 - Math.abs(gene_i - .5));
            }
            return moveFactor;
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
        public Map<String, GeneSpec> geneSpec() {
            Map<String, GeneSpec> geneMap = new HashMap<>();
            geneMap.put(STOPDISTANCEPARAM, new GeneParameterSpec(0, 1));
            geneMap.put(STOPSPEEDPARAM, new GeneParameterSpec(0, 1));
            for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
                geneMap.put(TUNE_GENE_NAME + i, new GeneParameterSpec(0, 1));
            }

            return geneMap;
        }
    }
}
