package no.bols.w1;

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Builder
public class World1SimulatorRunner {
    private int scenarioTimeMs = 100000;

    public SortedMap<Double, GeneMap> runAnalysisUntilStable(BrainFactory brainFactory) {
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newWorkStealingPool();
        Set<GeneMap> candidates = initializeInitialBrainGenes(brainFactory);
        SortedMap<Double, GeneMap> results = new TreeMap<>(Comparator.reverseOrder());
        simulateCandidatesAndAddToResults(brainFactory, executorService, candidates, results);
        int topScoreUnchangedNum = 0;
        Double topScore = 0.0;
        while (topScoreUnchangedNum < 20) {

            List<GeneMap> topList = results.values().stream().limit(16).collect(Collectors.toList());
            Iterator<GeneMap> topGenesIterator = topList.iterator();
            Set<GeneMap> newCandidates = new HashSet<>();
            for (int i = 0; i < topList.size() / 2; i++) {
                GeneMap parent1 = topGenesIterator.next();
                GeneMap parent2 = topGenesIterator.next();
                GeneMap offspring = parent1.breed(parent2);
                newCandidates.add(offspring);
            }
            simulateCandidatesAndAddToResults(brainFactory, executorService, newCandidates, results);
            if (topScore < results.firstKey()) {
                topScore = results.firstKey();
                topScoreUnchangedNum = 0;
            } else {
                topScoreUnchangedNum++;
            }
        }
        System.out.println("Stable result - #sim=" + results.size() + "(" + (System.currentTimeMillis() - startTime) / results.size() + " msec/sim). Best score " + results.firstKey() + " - " + results.get(results.firstKey()));
        return results;
    }

    private void simulateCandidatesAndAddToResults(BrainFactory brainFactory, ExecutorService executorService, Set<GeneMap> candidates, SortedMap<Double, GeneMap> results) {
        List<Future<Pair<Double, GeneMap>>> futures = candidates.stream()
                .map(gene -> executorService.submit(new SimulatorCallable(brainFactory, gene)))
                .collect(Collectors.toList());
        futures.forEach(f -> {
            try {
                Pair<Double, GeneMap> result = f.get();
                results.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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
            System.out.println("Running scenario " + genes.toString());
            Time time = new Time();
            World world = new World(time, brainFactory.createBrain(time, genes));
            time.runUntil(t -> world.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            return new Pair<>(world.score(), genes);
        }


    }

    private Set<GeneMap> initializeInitialBrainGenes(BrainFactory brainFactory) {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < 16; i++) {
            ret.add(brainFactory.randomGenes());
        }
        return ret;
    }
}
