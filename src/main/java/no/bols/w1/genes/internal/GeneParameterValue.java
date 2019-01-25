package no.bols.w1.genes.internal;//
//

import lombok.EqualsAndHashCode;

import java.util.Random;

@EqualsAndHashCode
public class GeneParameterValue extends GeneValue<Double> {


    private GeneParameterSpec geneParameterSpec;
    private final double value;
    private Random random = new Random();

    public GeneParameterValue(GeneParameterSpec geneParameterSpec, double childVal) {
        this.geneParameterSpec = geneParameterSpec;
        this.value = Math.max(geneParameterSpec.getMin(), Math.min(geneParameterSpec.getMax(), childVal));
    }

    @Override
    public GeneParameterValue breed(GeneValue other, double mutationChance) {
        GeneParameterValue otherValue = (GeneParameterValue) other;
        if (!chance(mutationChance)) {
            double diff = Math.max(value, otherValue.getValue()) - Math.min(value, otherValue.getValue());
            double average = (value + otherValue.getValue()) / 2.0;
            if (chance(mutationChance)) {
                return new GeneParameterValue(geneParameterSpec, average + random.nextDouble() * .2 - .1);
            } else {
                double childVal = average + (random.nextDouble() * 1.4 - .7) * diff;
                return new GeneParameterValue(geneParameterSpec, childVal);
            }
        } else {
            if (chance(.1)) {
                return new GeneParameterValue(geneParameterSpec, geneParameterSpec.getMin());
            }
            if (chance(.1)) {
                return new GeneParameterValue(geneParameterSpec, geneParameterSpec.getMax());
            }
            return geneParameterSpec.randomValue();
        }
    }

    protected boolean chance(double percent) {
        return random.nextDouble() < percent;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%3.2f", value);
    }
}
