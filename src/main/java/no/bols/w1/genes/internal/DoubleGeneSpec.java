package no.bols.w1.genes.internal;//
//

import lombok.Getter;
import no.bols.w1.genes.DoubleGene;

import java.lang.reflect.Field;
import java.util.Random;


public class DoubleGeneSpec extends GeneSpec<DoubleGeneValue> {

    @Getter
    private final double min;
    @Getter
    private final double max;

    public DoubleGeneSpec(DoubleGene annotation, Field field) {
        super(annotation, field);
        this.min = annotation.min();
        this.max = annotation.max();
    }


    public DoubleGeneValue randomValue() {
        return new DoubleGeneValue(this, new Random().nextDouble() * (max - min) + min);

    }


}
