package no.bols.w1.genes;//
//

import javafx.util.Pair;
import lombok.Builder;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public class Engine {
    private Function<GeneMap, Double> evalFunction;
    private Map<String, GeneSpec> geneSpecs;
    @Builder.Default
    private int initialPopulation = 64;
    @Builder.Default
    private int stableGenerationsLimit = 20;
    @Builder.Default
    private int generationUsableSize = 16;

    public SortedSet<Pair<Double, GeneMap>> runGeneticAlgorithmUntilStable() {
        long startTime = System.currentTimeMillis();
        int generations = 0;
        ExecutorService executorService = Executors.newWorkStealingPool();
        Set<GeneMap> candidates = initialPopulation();
        SortedSet<Pair<Double, GeneMap>> results = new TreeSet<>((e1, e2) -> compare(e1, e2));
        results.addAll(simulateCandidates(executorService, candidates));
        int topScoreUnchangedGenerations = 0;
        Double topScore = 0.0;
        while (topScoreUnchangedGenerations < stableGenerationsLimit) {
            generations++;
            List<Pair<Double, GeneMap>> topList = results.stream().limit(generationUsableSize).collect(Collectors.toList());

            Set<GeneMap> newGeneration = new HashSet<>();
            for (int i = 1; i < 5; i++) {                // Take 5 offspring of the top contenders
                newGeneration.add(topList.get(0).getValue().breed(topList.get(1).getValue()));
            }
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(generationUsableSize)).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2);
                newGeneration.add(offspring);
            });
            results.addAll(simulateCandidates(executorService, newGeneration));
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


    private List<Pair<Double, GeneMap>> simulateCandidates(ExecutorService executorService, Set<GeneMap> candidates) {
        return
                candidates.stream()
                        .map(gene -> executorService.submit(new SimulatorCallable(gene)))
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
        private GeneMap genes;

        public SimulatorCallable(GeneMap genes) {
            this.genes = genes;
        }

        @Override
        public Pair<Double, GeneMap> call() {
            //System.out.println("Running scenario " + genes.toString());
            return new Pair<>(evalFunction.apply(genes), genes);
        }

    }

    private Set<GeneMap> initialPopulation() {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < initialPopulation; i++) {
            GeneMap geneMap = new GeneMap();
            geneSpecs.entrySet().forEach(e -> geneMap.genes.put(e.getKey(), e.getValue().randomValue()));
            ret.add(geneMap);
        }
        return ret;
    }
}
