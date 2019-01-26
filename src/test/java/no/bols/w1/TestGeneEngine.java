package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.ToString;
import no.bols.w1.genes.DoubleGene;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class TestGeneEngine
        extends TestCase {
    public static final int TUNE_FACTOR_NUM = 5;


    public TestGeneEngine(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestGeneEngine.class);
    }

    public void testGeneSimulation() {
        World1SimulatorRunner simulator = World1SimulatorRunner.<TestGenes>builder()
                .scenarioTimeMs(25000)
                .brainFactory(new TestBrainFactory())
                .build();
        List<Pair<Double, TestGenes>> result = simulator.runGeneticAlgorithmUntilStable();
        TestGenes bestGene = result.get(0).getValue();
        for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
            assertEquals(.5f, bestGene.moveParam[i], .05);
        }
    }


    private class TestGenedBrain extends Brain {
        private final TestGenes genes;

        public TestGenedBrain(Time time, TestGenes genes) {
            super(time);
            this.genes = genes;
            time.scheduleRecurringEvent(t -> moveUntilNextToFood(t), 10);
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodProximityOutput() < genes.stopDistance) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf());
            } else {
                oneleg.motorOutput(genes.stopSpeed);
            }
        }

        private double calculateOutputWithMaxValueAchievedAtMoveParamHalf() {
            double moveFactor = 1;
            for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
                moveFactor = moveFactor * Math.max(0, 1 - Math.abs(genes.moveParam[i] - .5));
            }
            return moveFactor;
        }

    }

    @ToString
    public static class TestGenes {
        @DoubleGene(min = .0, max = 1.0)
        public double stopDistance;
        @DoubleGene(min = .0, max = 1.0)
        public double stopSpeed;

        @DoubleGene(min = .0, max = 1.0)
        public double[] moveParam = new double[TUNE_FACTOR_NUM];

    }

    private class TestBrainFactory implements BrainFactory<TestGenes> {

        @Override
        public Brain createBrain(Time time, TestGenes genes) {
            return new TestGenedBrain(time, genes);
        }

        @Override
        public TestGenes geneSpec() {
            return new TestGenes();
        }
    }
}
