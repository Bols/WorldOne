package no.bols.w1.genes.internal;//
//

import no.bols.w1.genes.EnumGene;

import java.lang.reflect.Field;
import java.util.Random;

public class EnumGeneSpec extends GeneSpec<EnumGeneValue> {

    private final Enum[] constants;

    public EnumGeneSpec(EnumGene annotation, Field field) {
        super(annotation, field);
        constants = (Enum[]) field.getType().getEnumConstants();
    }

    @Override
    public EnumGeneValue randomValue() {
        return new EnumGeneValue(this, constants[new Random().nextInt(constants.length)]);
    }
}
