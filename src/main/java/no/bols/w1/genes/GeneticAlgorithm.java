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
public class GeneticAlgorithm<S extends GeneScore> {

    @Builder.Default
    private int stableGenerationsLimit = 5;
    @Builder.Default
    private int generationUsableSize = 16;
    @Builder.Default
    private double mutationChance = .7;

    public void runGenerations(Map<String, GeneSpec> geneSpec, Function<List<GeneMap>, List<Pair<S, GeneMap>>> simulator, SortedSet<Pair<S, GeneMap>> results, Consumer<Pair<S, GeneMap>> bestScoreReceiver) {
        int numGenerations = 0;
        double effectiveMutationchance = this.mutationChance / geneSpec.size();
        int topScoreUnchangedGenerations = 0;
        S topScore = null;
        while (topScoreUnchangedGenerations < stableGenerationsLimit) {
            numGenerations++;
            System.out.println("---------- Generation " + numGenerations);
            List<Pair<S, GeneMap>> topList = results.stream().limit(generationUsableSize).collect(Collectors.toList());
            //            System.out.println("-------------------- Top list generation "+generations);
            //            for (Pair<S, GeneMap> sGeneMapPair : topList) {
            //                System.out.println(sGeneMapPair.getKey()+" "+sGeneMapPair.getValue());
            //            }
            //            System.out.println("-----------");


            List<GeneMap> newGeneration = new ArrayList<>();
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(Math.min(generationUsableSize, topList.size()))).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2, effectiveMutationchance);
                newGeneration.add(offspring);
            });
            List<Pair<S, GeneMap>> newResults = simulator.apply(newGeneration);
            int finalNumGenerations = numGenerations;
            newResults.forEach(untunedResult -> {
                        Pair<S, GeneMap> optimizedValue = untunedResult;//GradientDescent.<S>builder().gammaStartVal(5.0).precision(.5).build().runGradientDescent(geneSpec, gm -> simulator.apply(Collections.singletonList(gm)).get(0), untunedResult.getValue());
                        results.add(optimizedValue);
                    }
            );

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
        Pair<S, GeneMap> tunedTop = GradientDescent.<S>builder().gammaStartVal(.1).precision(.0001).build().runGradientDescent(geneSpec, gm -> simulator.apply(Collections.singletonList(gm)).get(0), results.first().getValue());
        if (tunedTop.getKey().getScore() > results.first().getKey().getScore()) {
            bestScoreReceiver.accept(tunedTop);
            results.add(tunedTop);
        }
    }
}
