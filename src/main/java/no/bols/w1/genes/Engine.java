package no.bols.w1.genes;//
//

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.internal.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public class Engine<G, S extends Comparable> {
    private Function<G, S> evalFunction;
    private G gene;
    @Builder.Default
    private int initialPopulation = 64;
    @Builder.Default
    private int stableGenerationsLimit = 20;
    @Builder.Default
    private int generationUsableSize = 16;
    @Builder.Default
    private double mutationChance = .2;
    private Consumer<Pair<S, G>> bestScoreReceiver;
    private Consumer<S> otherScoresReceiver;


    public List<Pair<S, G>> runGeneticAlgorithmUntilStable() {
        Map<String, GeneSpec> geneSpec = mapToGeneSpec(gene);
        long startTime = System.currentTimeMillis();
        int generations = 0;
        ExecutorService executorService = Executors.newWorkStealingPool();
        Set<GeneMap> candidates = initialPopulation(geneSpec);
        SortedSet<Pair<S, GeneMap>> results = new TreeSet<>((e1, e2) -> compare(e1, e2));
        results.addAll(simulateCandidates(executorService, candidates));
        int topScoreUnchangedGenerations = 0;
        S topScore = null;
        while (topScoreUnchangedGenerations < stableGenerationsLimit) {
            generations++;
            List<Pair<S, GeneMap>> topList = results.stream().limit(generationUsableSize).collect(Collectors.toList());

            Set<GeneMap> newGeneration = new HashSet<>();
            for (int i = 1; i < 5; i++) {                // Take 5 offspring of the top contenders
                newGeneration.add(topList.get(0).getValue().breed(topList.get(1).getValue(), mutationChance));
            }
            topList.forEach(parent1 ->
            {
                GeneMap parent2 = topList.get(new Random().nextInt(generationUsableSize)).getValue();
                GeneMap offspring = parent1.getValue().breed(parent2, mutationChance);
                newGeneration.add(offspring);
            });
            List<Pair<S, GeneMap>> newResults = simulateCandidates(executorService, newGeneration);
            results.addAll(newResults);
            if (topScore == null || topScore.compareTo(results.first().getKey()) < 0) {
                topScore = results.first().getKey();
                topScoreUnchangedGenerations = 0;
                GeneMap topGene = results.first().getValue();
                System.out.println("\nNew best score " + topScore + " - " + results.first().getValue().toString());
                if (bestScoreReceiver != null) {
                    bestScoreReceiver.accept(new Pair(topScore, mapToGene(topGene)));
                }
            } else {
                topScoreUnchangedGenerations++;
            }
            if (otherScoresReceiver != null) {
                for (Pair<S, GeneMap> otherResults : newResults) {
                    if (otherResults.getKey() != topScore) {
                        otherScoresReceiver.accept(otherResults.getKey());
                    }
                }
            }

        }
        System.out.println("Stable result - #sim=" + results.size() + "(" + (System.currentTimeMillis() - startTime) / results.size() + " msec/sim). " +
                "#Generations:" + generations + "  Best score " + results.first().getKey() + " - " + results.first().getValue());
        List<Pair<S, G>> ret = results.stream()
                .map(r -> new Pair<S, G>(r.getKey(), mapToGene(r.getValue())))
                .collect(Collectors.toList());
        return ret;
    }

    private Map<String, GeneSpec> mapToGeneSpec(G gene) {
        Map<String, GeneSpec> result = new HashMap<>();
        Map<Class, Class> annotationToSpecMap = new HashMap<>();
        annotationToSpecMap.put(DoubleGene.class, DoubleGeneSpec.class);
        annotationToSpecMap.put(BooleanGene.class, BooleanGeneSpec.class);
        for (Field field : gene.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Class<? extends GeneSpec> matchingSpec = annotationToSpecMap.get(annotation.annotationType());
                if (matchingSpec != null) {
                    try {
                        GeneSpec spec = matchingSpec.getConstructor(annotation.annotationType()).newInstance(annotation);
                        if (field.getType().isArray()) {
                            result.put(field.getName(), new ArrayGeneSpec(Array.getLength(field.get(gene)), spec));
                        } else {
                            result.put(field.getName(), spec);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        return result;
    }

    private G mapToGene(GeneMap spec) {
        try {
            Class<G> geneClass = (Class<G>) gene.getClass();
            G ret = geneClass.newInstance();
            for (String fieldName : spec.genes.keySet()) {
                spec.genes.get(fieldName).assignToField(geneClass.getDeclaredField(fieldName), ret);
            }
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int compare(Pair<S, GeneMap> e1, Pair<S, GeneMap> e2) {
        int scoreCompare = e2.getKey().compareTo(e1.getKey());
        if (scoreCompare != 0)
            return scoreCompare;
        return e1.getValue().equals(e2.getValue()) ? 0 : Integer.compare(e1.hashCode(), e2.hashCode());
    }


    private List<Pair<S, GeneMap>> simulateCandidates(ExecutorService executorService, Set<GeneMap> candidates) {
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


    public class SimulatorCallable implements Callable<Pair<S, GeneMap>> {
        private GeneMap genes;

        public SimulatorCallable(GeneMap genes) {
            this.genes = genes;
        }

        @Override
        public Pair<S, GeneMap> call() {
            //System.out.println("Running scenario " + genes.toString());
            return new Pair<>(evalFunction.apply(mapToGene(genes)), genes);
        }

    }

    private Set<GeneMap> initialPopulation(Map<String, GeneSpec> geneSpecs) {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < initialPopulation; i++) {
            GeneMap geneMap = new GeneMap();
            geneSpecs.entrySet().forEach(e -> geneMap.genes.put(e.getKey(), e.getValue().randomValue()));
            ret.add(geneMap);
        }
        return ret;
    }
}
