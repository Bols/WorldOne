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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Builder
public class Engine<G, S extends Comparable> {
    private Function<G, S> evalFunction;
    private G gene;
    @Builder.Default
    private int initialPopulation = 64;
    private Predicate<? super Pair<S, GeneMap>> filterInitialPopulation;

    private Consumer<Pair<S, G>> bestScoreReceiver;
    @Builder.Default
    private int parallellism = Runtime.getRuntime().availableProcessors();

    @Builder.Default
    private GeneticAlgorithm<S> geneticAlgorithm = GeneticAlgorithm.<S>builder().build();

    public List<Pair<S, G>> runGeneticAlgorithmUntilStable() {
        Map<String, GeneSpec> geneSpec = mapToGeneSpec(gene);
        ExecutorService executorService = Executors.newWorkStealingPool(parallellism);
        SortedSet<Pair<S, GeneMap>> results = new TreeSet<>((e1, e2) -> compare(e1, e2));
        findInitialCandidates(geneSpec, executorService, results);

        geneticAlgorithm.runGenerations(geneSpec, g -> simulateCandidates(executorService, g), results, r -> bestScoreReceiver.accept(new Pair<>(r.getKey(), mapToGene(r.getValue()))));

        List<Pair<S, G>> ret = results.stream()
                .map(r -> new Pair<S, G>(r.getKey(), mapToGene(r.getValue())))
                .collect(Collectors.toList());
        return ret;
    }



    private void findInitialCandidates(Map<String, GeneSpec> geneSpec, ExecutorService executorService, SortedSet<Pair<S, GeneMap>> results) {
        int numCandidateTrials = 0;
        long lastReport = System.currentTimeMillis() / 1000;
        do {
            Set<GeneMap> candidates = createInitialPopulation(geneSpec, parallellism);
            results.addAll(simulateCandidates(executorService, candidates).stream()
                    .filter(p -> filterInitialPopulation == null || filterInitialPopulation.test(p))
                    .collect(Collectors.toList()));
            numCandidateTrials = numCandidateTrials + candidates.size();
            if (System.currentTimeMillis() / 1000 > lastReport) {
                lastReport = System.currentTimeMillis() / 1000;
                System.out.println("Finding candidates, [" + results.size() + "/" + initialPopulation + "], " + numCandidateTrials + " attempts.");
            }
        } while (results.size() < initialPopulation);
        System.out.println("Found candidates, [" + results.size() + "/" + initialPopulation + "], " + numCandidateTrials + " attempts.");

    }

    private Map<String, GeneSpec> mapToGeneSpec(G gene) {
        Map<String, GeneSpec> result = new HashMap<>();
        Map<Class, Class> annotationToSpecMap = new HashMap<>();
        annotationToSpecMap.put(DoubleGene.class, DoubleGeneSpec.class);
        annotationToSpecMap.put(BooleanGene.class, BooleanGeneSpec.class);
        annotationToSpecMap.put(EnumGene.class, EnumGeneSpec.class);
        for (Field field : gene.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Class<? extends GeneSpec> matchingSpec = annotationToSpecMap.get(annotation.annotationType());
                if (matchingSpec != null) {
                    try {
                        GeneSpec spec = matchingSpec.getConstructor(annotation.annotationType(), Field.class).newInstance(annotation, field);
                        if (field.getType().isArray()) {
                            result.put(field.getName(), new ArrayGeneSpec(Array.getLength(field.get(gene)), spec, field, annotation));
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
            S score = evalFunction.apply(mapToGene(genes));
            //System.out.println(scoreValue.toString() + " " + genes.toString());
            return new Pair<>(score, genes);
        }

    }

    private Set<GeneMap> createInitialPopulation(Map<String, GeneSpec> geneSpecs, int numberOfCandidates) {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < numberOfCandidates; i++) {
            GeneMap geneMap = new GeneMap();
            geneSpecs.entrySet().forEach(e -> geneMap.genes.put(e.getKey(), e.getValue().randomValue()));
            ret.add(geneMap);
        }
        return ret;
    }
}
