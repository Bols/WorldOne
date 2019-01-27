package no.bols.w1.genes.internal;//
//

import lombok.Getter;
import no.bols.w1.genes.DoubleGene;

import java.util.Random;


public class DoubleGeneSpec extends GeneSpec<DoubleGeneValue> {

    @Getter
    private final double min;
    @Getter
    private final double max;

    public DoubleGeneSpec(DoubleGene annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    public DoubleGeneSpec(double min, double max) {
        this.min = min;
        this.max = max;
    }


    public DoubleGeneValue randomValue() {
        return new DoubleGeneValue(this, new Random().nextDouble() * (max - min) + min);

    }


}
