package no.bols.w1.genes.internal;//
//

import javafx.util.Pair;
import no.bols.w1.genes.GeneScore;

import java.util.function.Function;

public class DoubleGeneValue extends GeneValue {


    private DoubleGeneSpec geneParameterSpec;
    private final double value;

    public DoubleGeneValue(DoubleGeneSpec geneParameterSpec, double childVal) {
        this.geneParameterSpec = geneParameterSpec;
        this.value = Math.max(geneParameterSpec.getMin(), Math.min(geneParameterSpec.getMax(), childVal));
    }

    @Override
    public DoubleGeneValue breed(GeneValue other, double mutationChance) {
        DoubleGeneValue otherValue = (DoubleGeneValue) other;
        if (!chance(mutationChance)) {
            double diff = Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue());
            double average = (value + otherValue.getValue()) / 2.0;
            if (chance(mutationChance)) {
                return new DoubleGeneValue(geneParameterSpec, average + random.nextDouble() * .2 - .1);
            } else {
                double childVal = average + (random.nextDouble() * 1.4 - .7) * diff;
                return new DoubleGeneValue(geneParameterSpec, childVal);
            }
        } else {
            if (chance(.1)) {
                return new DoubleGeneValue(geneParameterSpec, geneParameterSpec.getMin());
            }
            if (chance(.1)) {
                return new DoubleGeneValue(geneParameterSpec, geneParameterSpec.getMax());
            }
            return geneParameterSpec.randomValue();
        }
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%3.2f", value);
    }

    @Override
    public GeneValue nextIncrementalValueForGradientDescent(double presentScore, double gamma, Function<GeneValue, Pair<? extends GeneScore, GeneMap>> simulateChangedValue) {
        final double range = geneParameterSpec.getMax() - geneParameterSpec.getMin();
        final double infinitesimalChange = gamma * range / 20;
        double infitesimalVal = this.value + infinitesimalChange;
        double scoreWithInfChange = simulateChangedValue.apply(new DoubleGeneValue(this.geneParameterSpec, infitesimalVal)).getKey().getScore();
        double infinitesimalScore = scoreWithInfChange - presentScore;
        double differential = infinitesimalScore / infinitesimalChange;
        double nextValCandidate = this.value + differential * gamma * range;
        double scoreWithCalculatedDiff = simulateChangedValue.apply(new DoubleGeneValue(this.geneParameterSpec, nextValCandidate)).getKey().getScore();
        if (presentScore > scoreWithCalculatedDiff && presentScore > scoreWithInfChange) {
            nextValCandidate = this.value;
        } else {
            if (scoreWithInfChange > scoreWithCalculatedDiff) {
                nextValCandidate = infitesimalVal;
            }
        }
        return new DoubleGeneValue(this.geneParameterSpec, nextValCandidate);
    }
}
