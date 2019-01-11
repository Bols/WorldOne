package no.bols.w1;

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Builder
public class World1SimulatorRunner {
    private int scenarioTimeMs = 100000;

    public SortedSet<Pair<Double, GeneMap>> runGeneticAlgorithmUntilStable(BrainFactory brainFactory) {
        long startTime = System.currentTimeMillis();
        int generations = 0;
        ExecutorService executorService = Executors.newWorkStealingPool();
        Set<GeneMap> candidates = initializeInitialBrainGenes(brainFactory);
        SortedSet<Pair<Double, GeneMap>> results = new TreeSet<>((e1, e2) -> compare(e1, e2));
        results.addAll(simulateCandidates(brainFactory, executorService, candidates));
        int topScoreUnchangedGenerations = 0;
        Double topScore = 0.0;
        while (topScoreUnchangedGenerations < 20) {
            generations++;
            List<Pair<Double, GeneMap>> topList = results.stream().limit(16).collect(Collectors.toList());

            Set<GeneMap> newGeneration = new HashSet<>();
            for (int i = 1; i < 5; i++) {                // Take 5 offspring of the top contenders
                newGeneration.add(topList.get(0).getValue().breed(topList.get(1).getValue()));
            }
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(16)).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2);
                newGeneration.add(offspring);
            });
            results.addAll(simulateCandidates(brainFactory, executorService, newGeneration));
            if (topScore < results.first().getKey()) {
                topScore = results.first().getKey();
                topScoreUnchangedGenerations = 0;
                System.out.println("\nNew best score " + topScore + " - " + results.first().getValue().toString());
            } else {
                topScoreUnchangedGenerations++;
                //System.out.print("#");
            }
        }
        System.out.println("Stable result - #sim=" + results.size() + "(" + (System.currentTimeMillis() - startTime) / results.size() + " msec/sim). #Generations:" + generations + "  Best score " + results.first().getKey() + " - " + results.first().getValue());
        return results;
    }

    private int compare(Pair<Double, GeneMap> e1, Pair<Double, GeneMap> e2) {
        int scoreCompare = e2.getKey().compareTo(e1.getKey());
        if (scoreCompare != 0)
            return scoreCompare;
        return e1.getValue().equals(e2.getValue()) ? 0 : Integer.compare(e1.hashCode(), e2.hashCode());
    }


    private List<Pair<Double, GeneMap>> simulateCandidates(BrainFactory brainFactory, ExecutorService executorService, Set<GeneMap> candidates) {
        return
                candidates.stream()
                .map(gene -> executorService.submit(new SimulatorCallable(brainFactory, gene)))
                        .collect(Collectors.toList())
                        .stream().
                        map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
                        }).collect(Collectors.toList());
    }


    public class SimulatorCallable implements Callable<Pair<Double, GeneMap>> {
        private BrainFactory brainFactory;
        private GeneMap genes;

        public SimulatorCallable(BrainFactory brainFactory, GeneMap genes) {
            this.brainFactory = brainFactory;
            this.genes = genes;
        }

        @Override
        public Pair<Double, GeneMap> call() {
            //System.out.println("Running scenario " + genes.toString());
            Time time = new Time();
            Brain brain = brainFactory.createBrain(time, genes);
            Double score = runScenarioTrainingUntilStable(time, brain);
            System.out.print("Score " + score + ", genes " + genes.toString() + "\n");
            return new Pair<>(score, genes);
        }


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

    private Set<GeneMap> initializeInitialBrainGenes(BrainFactory brainFactory) {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < 64; i++) {
            ret.add(brainFactory.randomGenes());
        }
        return ret;
    }

}
