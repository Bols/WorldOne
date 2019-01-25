package no.bols.w1;

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.Engine;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.List;


@Builder
public class World1SimulatorRunner<T> {
    private int scenarioTimeMs = 100000;
    BrainFactory<T> brainFactory;

    private double evaluate(T genes) {
        Time time = new Time();
        Brain brain = brainFactory.createBrain(time, genes);
        Double score = runScenarioTrainingUntilStable(time, brain);
        return score;
    }

    private double runScenarioTrainingUntilStable(Time time, Brain brain) {
        double topScore = 0;
        double previousScore = 0;
        do {
            topScore = previousScore;
            time.reset();
            World simulationWorld = new World(time, brain);
            time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            //System.out.println("New score "+simulationWorld.score()+" prev score "+topScore);
            previousScore = simulationWorld.score();
            if (topScore > 0 && previousScore > topScore) {
                System.out.println("Improvement " + topScore + " -> " + previousScore);
                throw new RuntimeException(("Improvement!"));
            }
        } while (previousScore > topScore);
        return topScore;
    }


    public List<Pair<Double, T>> runGeneticAlgorithmUntilStable() {
        return Engine.<T>builder()
                .initialPopulation(100)
                .generationUsableSize(20)
                .mutationChance(.2)
                .evalFunction(this::evaluate)
                .gene(brainFactory.geneSpec())
                .build()
                .runGeneticAlgorithmUntilStable();
    }
}
