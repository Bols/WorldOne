package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.ToString;
import no.bols.w1.genes.DoubleGene;
import no.bols.w1.genes.EnumGene;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

/**
 * Unit test for simple App.
 */
public class TestGeneEngine
        extends TestCase {
    public static final int TUNE_FACTOR_NUM = 10;


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
        Pair<WorldScoreWithTrainingHistory, TestGenes> result = simulator.runGeneticAlgorithmUntilStable();
        TestGenes bestGene = result.getValue();
        for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
            assertEquals(.5f, bestGene.moveParam[i], .05);
        }
        assertTrue(bestGene.speedEnum == SpeedGene.fast);
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
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf() * genes.speedEnum.speedFactor);
            } else {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf() * genes.stopSpeed / 4);
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

        @EnumGene
        public SpeedGene speedEnum;


    }

    public enum SpeedGene {
        slow(.3), medium(.5), fast(1.0);
        private double speedFactor;

        SpeedGene(double speedFactor) {
            this.speedFactor = speedFactor;
        }
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
