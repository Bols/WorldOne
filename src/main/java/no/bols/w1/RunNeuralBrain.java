package no.bols.w1;//
//

import javafx.util.Pair;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.NeuralBrainFactory;
import no.bols.w1.genes.internal.GeneMap;
import no.bols.w1.physics.Time;

import java.util.Locale;
import java.util.stream.Collectors;

public class RunNeuralBrain {
    public static void main(String[] argv) {
        Locale.setDefault(Locale.US);
        World1SimulatorRunner simulator = World1SimulatorRunner.<BrainGene>builder()
                .scenarioTimeMs(25000)
                .brainFactory(new NeuralBrainFactory())

                .build();
        Pair<WorldScoreWithTrainingHistory, GeneMap> bestResult = simulator.runGeneticAlgorithmUntilStable();
        for(int i =0;i<10;i++){
            WorldScoreWithTrainingHistory rerunScore = simulator.createBrainAndRunScenarioUntilStable(bestResult.getValue());
            System.out.println("Rerun "+i+": "+rerunScore.toString());
        }


        Double topScore = bestResult.getKey().score().getScoreValue();
        System.out.println("----------- Result stats ----------------");
        System.out.println(bestResult.getKey());
        System.out.println("Real-clock runtime:  " + bestResult.getKey().getTime().getRealClockRuntime());
        System.out.println("Events handled: " + bestResult.getKey().getTime().getEventsHandled());
        System.out.println("Stats: " + bestResult.getKey().getTime().getStats().entrySet().stream().map(e -> e.getKey() + ":" + e.getValue().doubleValue()).collect(Collectors.joining(",")));

        WorldScoreWithTrainingHistory worldScoreWithTrainingHistory = simulator.rerunAndVisualize(bestResult);

        System.out.println("Score " + topScore + " (visualization rerun: " + worldScoreWithTrainingHistory.score().getScoreValue() + ")");
        if(Math.abs(topScore-worldScoreWithTrainingHistory.score().getScoreValue())>.1){
            throw new RuntimeException("Large difference between original and rerun-score");
        }
    }
}
