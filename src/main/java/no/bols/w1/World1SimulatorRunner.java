package no.bols.w1;

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.Engine;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.SortedSet;


@Builder
public class World1SimulatorRunner {
    private int scenarioTimeMs = 100000;
    BrainFactory brainFactory;

    private double evaluate(GeneMap genes) {
        Time time = new Time();
        Brain brain = brainFactory.createBrain(time, genes);
        Double score = runScenarioTrainingUntilStable(time, brain);
        //System.out.print("Score " + score + ", genes " + genes.toString() + "\n");
        return score;
    }

    private double runScenarioTrainingUntilStable(Time time, Brain brain) {
        double topScore = 0;
        double previousScore = 0;
        do {
            topScore = previousScore;
            time.reset();
            brain.initializeRecurringInputEvents();
            World simulationWorld = new World(time, brain);
            time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            //System.out.println("New score "+simulationWorld.score()+" prev score "+topScore);
            previousScore = simulationWorld.score();
        } while (previousScore > topScore);
        return topScore;
    }


    public SortedSet<Pair<Double, GeneMap>> runGeneticAlgorithmUntilStable() {
        return Engine.builder()
                .initialPopulation(100)
                .generationUsableSize(20)
                .mutationChance(.2)
                .evalFunction(this::evaluate)
                .geneSpecs(brainFactory.geneSpec())
                .build()
                .runGeneticAlgorithmUntilStable();
    }
}
