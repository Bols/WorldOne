package no.bols.w1.genes;//
//

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.internal.GeneMap;
import no.bols.w1.genes.internal.GeneSpec;
import no.bols.w1.genes.internal.GeneValue;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.AbstractMap.Entry;
import static java.util.AbstractMap.SimpleEntry;

@Builder
public class GradientDescent<S extends GeneScore> {
    @Builder.Default
    double gammaStartVal = 5.0;

    @Builder.Default
    double precision = 0.01;

    public Pair<S, GeneMap> runGradientDescent(Map<String, GeneSpec> geneSpec, Function<GeneMap, Pair<S, GeneMap>> simulator, GeneMap initialValues) {
        double gamma = gammaStartVal;
        GeneMap current = initialValues;
        S currentScore = null;
        do {
            final GeneMap finalCurrent = current;
            final S presentScore = simulator.apply(finalCurrent).getKey();
            S newScore;
            GeneMap candidate;
            do {
                double finalGamma = gamma;
                System.out.println("\tCalculating Diff");
                Map<String, GeneValue> nextStep = current.genes.entrySet().stream()
                        .parallel()
                        .map(e -> new SimpleEntry<>(e.getKey(),
                                    e.getValue().nextIncrementalValueForGradientDescent(presentScore.getScore(), finalGamma,
                                            changedGeneValue -> simulateChange(simulator, changedGeneValue, e.getKey(), finalCurrent))))
                        .collect(Collectors.toMap(
                                Entry::getKey,
                                Entry::getValue
                        ));
                candidate = new GeneMap(nextStep);

                newScore = simulator.apply(candidate).getKey();
                if (newScore.getScore() <= presentScore.getScore()) {
                    gamma = gamma / 2;
                }
                System.out.println("New iteration, gamma = " + gamma);

            } while (gamma > precision && newScore.getScore() < presentScore.getScore());
            currentScore = newScore;
            current = candidate;
        } while (gamma > precision);
        return new Pair<>(currentScore, current);
    }

    private Pair<S, GeneMap> simulateChange(Function<GeneMap, Pair<S, GeneMap>> simulator, GeneValue changedGeneValue, String geneName, GeneMap presentPoint) {
        GeneMap modifiedMap = presentPoint.clone();
        modifiedMap.setValue(geneName, changedGeneValue);
        return simulator.apply(modifiedMap);
    }


}
