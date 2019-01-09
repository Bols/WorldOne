package no.bols.w1.genes;//
//

import lombok.Getter;

import java.util.Random;

public class GeneParameterValue extends Gene {


    private final float min;
    private final float max;
    @Getter
    private final float value;

    public GeneParameterValue(float min, float max) {
        this.min = min;
        this.max = max;
        this.value = new Random().nextFloat() * (max - min) + min;
    }

    public GeneParameterValue(float min, float max, float childVal) {
        this.min = min;
        this.max = max;
        this.value = childVal;
    }

    @Override
    public GeneParameterValue breed(Gene other) {
        GeneParameterValue otherValue = (GeneParameterValue) other;
        boolean mutation = new Random().nextFloat() < MUTATION_CHANCE;
        if (!mutation) {
            float childVal = new Random().nextFloat() * (Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue())) + Math.min(value, otherValue.getValue());
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
