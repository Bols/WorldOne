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

    public List<Pair<Double, GeneMap>> runGeneticAlgorithmUntilStable(BrainFactory brainFactory) {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newWorkStealingPool(1);
        Set<GeneMap> candidates = initializeInitialBrainGenes(brainFactory);
        List<Pair<Double, GeneMap>> results = new ArrayList<>();
        results.addAll(simulateCandidates(brainFactory, executorService, candidates));
        int topScoreUnchangedNum = 0;
        Double topScore = 0.0;
        while (topScoreUnchangedNum < 20) {
            results.sort((p1, p2) -> p2.getKey().compareTo(p1.getKey()));
            List<Pair<Double, GeneMap>> topList = results.stream().limit(16).collect(Collectors.toList());

            Set<GeneMap> newCandidates = new HashSet<>();
            newCandidates.add(topList.get(0).getValue().breed(topList.get(1).getValue()));
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(16)).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2);
                newCandidates.add(offspring);
            });
            results.addAll(simulateCandidates(brainFactory, executorService, newCandidates));
            results.sort((p1, p2) -> p2.getKey().compareTo(p1.getKey()));
            if (topScore < results.get(0).getKey()) {
                topScore = results.get(0).getKey();
                topScoreUnchangedNum = 0;
                System.out.println("\nNew best score " + topScore + " - " + results.get(0).getValue().toString());
            } else {
                topScoreUnchangedNum++;
                //System.out.print("#");
            }
        }
        System.out.println("Stable result - #sim=" + results.size() + "(" + (System.currentTimeMillis() - startTime) / results.size() + " msec/sim). Best score " + results.get(0).getKey() + " - " + results.get(0).getValue());
        return results;
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
            brain.initalizeTime();
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
