package no.bols.w1.genes;//
//

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.internal.GeneMap;
import no.bols.w1.genes.internal.GeneSpec;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public class GeneticAlgorithm<S extends Comparable> {

    @Builder.Default
    private int stableGenerationsLimit = 20;
    @Builder.Default
    private int generationUsableSize = 16;
    @Builder.Default
    private double mutationChance = .3;

    public void runGenerations(Map<String, GeneSpec> geneSpec, Function<Set<GeneMap>, List<Pair<S, GeneMap>>> simulator, SortedSet<Pair<S, GeneMap>> results, Consumer<Pair<S, GeneMap>> bestScoreReceiver) {
        long startTime = System.currentTimeMillis();
        int numGenerations = 0;
        double effectiveMutationchance = this.mutationChance / geneSpec.size();
        int topScoreUnchangedGenerations = 0;
        S topScore = null;
        while (topScoreUnchangedGenerations < stableGenerationsLimit) {
            numGenerations++;
            List<Pair<S, GeneMap>> topList = results.stream().limit(generationUsableSize).collect(Collectors.toList());
            //            System.out.println("-------------------- Top list generation "+generations);
            //            for (Pair<S, GeneMap> sGeneMapPair : topList) {
            //                System.out.println(sGeneMapPair.getKey()+" "+sGeneMapPair.getValue());
            //            }
            //            System.out.println("-----------");


            Set<GeneMap> newGeneration = new HashSet<>();
            for (int i = 1; i < 5; i++) {                // Take 5 offspring of the top contenders
                newGeneration.add(topList.get(0).getValue().breed(topList.get(1).getValue(), effectiveMutationchance));
            }
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(generationUsableSize)).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2, effectiveMutationchance);
                newGeneration.add(offspring);
            });
            List<Pair<S, GeneMap>> newResults = simulator.apply(newGeneration);
            results.addAll(newResults);
            if (topScore == null || topScore.compareTo(results.first().getKey()) < 0) {
                topScore = results.first().getKey();
                topScoreUnchangedGenerations = 0;
                GeneMap topGene = results.first().getValue();
                System.out.println("\nNew best scoreValue " + topScore + " - " + results.first().getValue().toString());
                if (bestScoreReceiver != null) {
                    bestScoreReceiver.accept(new Pair(topScore, topGene));
                }
            } else {
                topScoreUnchangedGenerations++;
            }


        }
        System.out.println("Stable result - #sim=" + results.size() + "(" + (System.currentTimeMillis() - startTime) / results.size() + " msec/sim). " +
                "#Generations:" + numGenerations + "  Best scoreValue " + results.first().getKey() + " - " + results.first().getValue());

    }
}
