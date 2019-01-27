package no.bols.w1.genes.internal;//
//

import no.bols.w1.genes.BooleanGene;


public class BooleanGeneSpec extends GeneSpec<BooleanGeneValue> {


    public BooleanGeneSpec(BooleanGene annotation) {
    }


    public BooleanGeneValue randomValue() {
        return new BooleanGeneValue(this);

    }


}
