package no.bols.w1.genes;//
//

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Random;

@EqualsAndHashCode
public class GeneParameterValue extends Gene {


    private GeneParameterSpec geneParameterSpec;
    @Getter
    private final double value;

    public GeneParameterValue(GeneParameterSpec geneParameterSpec, double childVal) {
        this.geneParameterSpec = geneParameterSpec;
        this.value = Math.max(geneParameterSpec.getMin(), Math.min(geneParameterSpec.getMax(), childVal));
    }

    @Override
    public GeneParameterValue breed(Gene other, double mutationChance) {
        GeneParameterValue otherValue = (GeneParameterValue) other;
        boolean mutation = new Random().nextDouble() < mutationChance;
        if (!mutation) {
            boolean minorMutation = new Random().nextDouble() < mutationChance;
            double diff = Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue());
            double average = (value + otherValue.getValue()) / 2.0;
            if (minorMutation) {
                return new GeneParameterValue(geneParameterSpec, average + new Random().nextDouble() * .2 - .1);
            } else {
                double childVal = average + (new Random().nextDouble() * 1.4 - .7) * diff;
                return new GeneParameterValue(geneParameterSpec, childVal);
            }
        } else {
            return geneParameterSpec.randomValue();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
