package no.bols.w1.genes;//
//

import lombok.Getter;

import java.util.Random;

public class GeneParameterValue extends Gene {


    private final double min;
    private final double max;
    @Getter
    private final double value;

    public GeneParameterValue(double min, double max) {
        this.min = min;
        this.max = max;
        this.value = new Random().nextFloat() * (max - min) + min;
    }

    public GeneParameterValue(double min, double max, double childVal) {
        this.min = min;
        this.max = max;
        this.value = childVal;
    }

    @Override
    public GeneParameterValue breed(Gene other) {
        GeneParameterValue otherValue = (GeneParameterValue) other;
        boolean mutation = new Random().nextDouble() < MUTATION_CHANCE;
        if (!mutation) {
            double childVal = new Random().nextDouble() * (Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue())) + Math.min(value, otherValue.getValue());
            return new GeneParameterValue(min, max, childVal);
        } else {
            return new GeneParameterValue(min, max);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
