package no.bols.w1.genes.internal;//
//

import java.util.Random;

public class BooleanGeneValue extends GeneValue {


    private BooleanGeneSpec booleanGeneSpec;
    private final boolean value;
    private static Random random = new Random();

    public BooleanGeneValue(BooleanGeneSpec booleanGeneSpec, boolean childVal) {
        this.booleanGeneSpec = booleanGeneSpec;
        this.value = childVal;
    }

    public BooleanGeneValue(BooleanGeneSpec booleanGeneSpec) {
        this(booleanGeneSpec, random.nextBoolean());
    }

    @Override
    public BooleanGeneValue breed(GeneValue other, double mutationChance) {
        BooleanGeneValue otherValue = (BooleanGeneValue) other;
        if (!chance(mutationChance)) {
            return chance(.5) ? this : otherValue;
        } else {
            return new BooleanGeneValue(booleanGeneSpec, random.nextBoolean());
        }
    }

    protected boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
