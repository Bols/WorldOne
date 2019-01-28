package no.bols.w1.genes.internal;//
//

import no.bols.w1.genes.BooleanGene;

import java.lang.reflect.Field;


public class BooleanGeneSpec extends GeneSpec<BooleanGeneValue> {


    public BooleanGeneSpec(BooleanGene annotation, Field field) {
        super(annotation, field);
    }


    public BooleanGeneValue randomValue() {
        return new BooleanGeneValue(this);

    }


}
