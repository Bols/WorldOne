package no.bols.w1;

import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class World1SimulatorApplication {

    public SortedMap<Double, GeneMap> runAnalysisUntilStable(BrainFactory brainFactory) {
        Set<GeneMap> candidates = initializeInitialBrainGenes(brainFactory);
        SortedMap<Double, GeneMap> results = new TreeMap<>(Comparator.reverseOrder());
        candidates.forEach(gene -> {
            Double score = new SimulatorCallable(brainFactory, gene).call();
            results.put(score, gene);
        });
        int topScoreUnchangedNum = 0;
        Double topScore = 0.0;
        while (topScoreUnchangedNum < 3) {

            Iterator<GeneMap> topGenes = results.values().stream().limit(10).collect(Collectors.toList()).iterator();
            for (int i = 0; i < 5; i++) {
                GeneMap parent1 = topGenes.next();
                GeneMap parent2 = topGenes.next();
                GeneMap offspring = parent1.breed(parent2);
                Double score = new SimulatorCallable(brainFactory, offspring).call();
                results.put(score, offspring);
            }

            if (topScore < results.firstKey()) {
                topScore = results.firstKey();
                topScoreUnchangedNum = 0;
            } else {
                topScoreUnchangedNum++;
            }
        }
        return results;
    }


    public static class SimulatorCallable implements Callable<Double> {
        private BrainFactory brainFactory;
        private GeneMap genes;

        public SimulatorCallable(BrainFactory brainFactory, GeneMap genes) {
            this.brainFactory = brainFactory;
            this.genes = genes;
        }

        @Override
        public Double call() {
            System.out.println("Running scenario " + genes.toString());
            Time time = new Time();
            World world = new World(time, brainFactory.createBrain(time, genes));
            time.runUntil(t -> world.getTime().getTimeMicroSeconds() > 100000000);
            System.out.println("Score " + world.score());
            return world.score();
        }


    }

    public Set<GeneMap> initializeInitialBrainGenes(BrainFactory brainFactory) {
        HashSet<GeneMap> ret = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ret.add(brainFactory.randomGenes());
        }
        return ret;
    }
}
