package no.bols.w1;

import javafx.util.Pair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import no.bols.w1.ai.NeuralBrainFactory;
import no.bols.w1.genes.GeneMap;

import java.util.SortedSet;

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
        World1SimulatorRunner simulator = World1SimulatorRunner.builder()
                .scenarioTimeMs(25000)
                .brainFactory(new NeuralBrainFactory())
                .build();
        SortedSet<Pair<Double, GeneMap>> result = simulator.runGeneticAlgorithmUntilStable();
        Double topScore = result.first().getKey();
        assertTrue("Score is too low: " + topScore, topScore > 2.0);
    }

}
