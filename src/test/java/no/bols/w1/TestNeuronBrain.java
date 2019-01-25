package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.NeuralBrainFactory;
import no.bols.w1.genes.internal.GeneMap;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class TestNeuronBrain
        extends TestCase {

    public TestNeuronBrain(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestNeuronBrain.class);
    }

    public void testNeuronBrainScore() {
        World1SimulatorRunner simulator = World1SimulatorRunner.<BrainGene>builder()
                .scenarioTimeMs(25000)
                .brainFactory(new NeuralBrainFactory())
                .build();
        List<Pair<WorldScoreWithTrainingHistory, GeneMap>> result = simulator.runGeneticAlgorithmUntilStable();
        Double topScore = result.get(0).getKey().getBestScore().getScore();
        assertTrue("Score is too low: " + topScore, topScore > 2.0);
    }

}
