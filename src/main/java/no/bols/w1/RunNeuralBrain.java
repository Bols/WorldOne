package no.bols.w1;//
//

import javafx.util.Pair;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.NeuralBrainFactory;
import no.bols.w1.genes.internal.GeneMap;

public class RunNeuralBrain {
    public static void main(String[] argv) {
        World1SimulatorRunner simulator = World1SimulatorRunner.<BrainGene>builder()
                .scenarioTimeMs(25000)
                .brainFactory(new NeuralBrainFactory())

                .build();
        Pair<WorldScoreWithTrainingHistory, GeneMap> result = simulator.runGeneticAlgorithmUntilStable();
        WorldScoreWithTrainingHistory worldScoreWithTrainingHistory = simulator.rerunAndVisualize(result);

        Double topScore = result.getKey().score().getScoreValue();
        System.out.println("Score " + topScore + " (visualization rerun: " + worldScoreWithTrainingHistory.score().getScoreValue() + ")");
    }
}
