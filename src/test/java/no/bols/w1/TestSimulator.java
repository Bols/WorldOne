package no.bols.w1;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

/**
 * Unit test for simple App.
 */
public class TestSimulator
        extends TestCase {
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
        EvolutionResult<DoubleGene, Double> result = simulator.runGeneticAlgorithmUntilStable();
        Phenotype<DoubleGene, Double> bestPhenotype = result.getBestPhenotype();
        System.out.println(result.getBestFitness() + " " + bestPhenotype.getGenotype().toString());

        assertEquals(.5f, bestPhenotype.getGenotype().getChromosome(2).getGene().doubleValue(), .05);
    }


    private class TestGenedBrain extends Brain {
        private final double stopDistanceParam;
        private final double stopSpeedParam;
        private final Genotype<DoubleGene> geneMap;

        public TestGenedBrain(Time time, Genotype<DoubleGene> geneMap) {
            super(time);
            stopDistanceParam = geneMap.get(0).getGene().doubleValue();
            stopSpeedParam = geneMap.get(1).getGene().doubleValue();
            this.geneMap = geneMap;
        }

        private void moveUntilNextToFood(Time time) {
            if (oneleg.getFoodDistanceOutput() > stopDistanceParam) {
                oneleg.motorOutput(calculateOutputWithMaxValueAchievedAtMoveParamHalf());
            } else {
                oneleg.motorOutput(stopSpeedParam);
            }
        }

        private double calculateOutputWithMaxValueAchievedAtMoveParamHalf() {
            double moveFactor = 1;
            for (int i = 0; i < TUNE_FACTOR_NUM; i++) {
                double gene_i = geneMap.get(2).getGene(i).doubleValue();
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
        public Brain createBrain(Time time, Genotype<DoubleGene> genes) {
            return new TestGenedBrain(time, genes);
        }

        @Override
        public Factory<Genotype<DoubleGene>> genotypeFactory() {
            return Genotype
                    .of(DoubleChromosome.of(0.0, 1.0),
                            DoubleChromosome.of(0.0, 1.0),
                            DoubleChromosome.of(0.0, 1.0, TUNE_FACTOR_NUM));
        }
    }
}
