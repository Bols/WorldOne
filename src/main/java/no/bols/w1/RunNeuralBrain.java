package no.bols.w1;//
//

import javafx.util.Pair;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.NeuralBrainFactory;
import no.bols.w1.genes.internal.GeneMap;

import java.util.Locale;
import java.util.stream.Collectors;

public class RunNeuralBrain {
    public static void main(String[] argv) {
        Locale.setDefault(Locale.US);
        World1SimulatorRunner simulator = World1SimulatorRunner.<BrainGene>builder()
                .scenarioTimeMs(25000)
                .brainFactory(new NeuralBrainFactory())

                .build();
        long startTime = System.currentTimeMillis();
        Pair<WorldScoreWithTrainingHistory, GeneMap> bestResult = simulator.runGeneticAlgorithmUntilStable();
        for(int i =0;i<10;i++){
            WorldScoreWithTrainingHistory rerunScore = simulator.createBrainAndRunScenarioUntilStable(bestResult.getValue());
            System.out.println("Rerun "+i+": "+rerunScore.toString());
        }


        Double topScore = bestResult.getKey().score().getScoreValue();
        System.out.println("----------- Result stats ----------------");
        System.out.println(bestResult.getKey());
        System.out.println("Real-clock runtime for best simulation:  " + (bestResult.getKey().getTime().getRealClockRuntimeNs()/1000)/1000.0+"ms.  Events handled:"+bestResult.getKey().getTime().getEventsHandled());
        System.out.println("Total runtime " + (System.currentTimeMillis()-startTime)+"ms");

        simulator.rerunAndVisualize(bestResult);


    }
}
