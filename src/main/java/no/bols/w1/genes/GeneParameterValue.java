package no.bols.w1.genes;//
//

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Random;

@EqualsAndHashCode
public class GeneParameterValue extends Gene {


    private final double min;
    private final double max;
    @Getter
    private final double value;

    public GeneParameterValue(double min, double max) {
        this.min = min;
        this.max = max;
        this.value = new Random().nextDouble() * (max - min) + min;
    }

    public GeneParameterValue(double min, double max, double childVal) {
        this.min = min;
        this.max = max;
        this.value = Math.max(min, Math.min(max, childVal));
    }

    @Override
    public GeneParameterValue breed(Gene other) {
        GeneParameterValue otherValue = (GeneParameterValue) other;
        boolean mutation = new Random().nextDouble() < MUTATION_CHANCE;
        if (!mutation) {
            boolean minorMutation = new Random().nextDouble() < MUTATION_CHANCE;
            double diff = Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue());
            double average = (value + otherValue.getValue()) / 2.0;
            if (minorMutation) {
                return new GeneParameterValue(min, max, average + new Random().nextDouble() * MUTATION_CHANCE - (MUTATION_CHANCE / 2.0));
            } else {
                double childVal = (new Random().nextDouble() * diff) - (diff / 2.0) + average;
                return new GeneParameterValue(min, max, childVal);
            }
        } else {
            return new GeneParameterValue(min, max);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
