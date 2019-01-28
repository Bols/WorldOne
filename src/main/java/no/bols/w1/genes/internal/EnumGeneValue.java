package no.bols.w1.genes.internal;//
//

public class EnumGeneValue extends GeneValue<Enum> {
    private EnumGeneSpec spec;
    private Enum value;

    public EnumGeneValue(EnumGeneSpec spec, Enum value) {
        this.spec = spec;
        this.value = value;
    }

    @Override
    public GeneValue breed(GeneValue other, double mutationChance) {
        if (chance(mutationChance)) {
            return spec.randomValue();
        }
        if (chance(.5)) {
            return this;
        }
        return other;
    }

    @Override
    public Enum getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.name();
    }
}
