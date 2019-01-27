package no.bols.w1.genes.internal;//
//

import lombok.Getter;

import java.lang.reflect.Array;

public class ArrayGeneSpec<T extends GeneSpec> extends GeneSpec<ArrayGeneValue> {
    @Getter
    private final int length;
    @Getter
    private final GeneSpec elementSpec;

    public ArrayGeneSpec(int length, GeneSpec elementSpec) {
        this.length = length;
        this.elementSpec = elementSpec;
    }


    @Override
    public ArrayGeneValue randomValue() {
        Class<? extends GeneValue> elementType = elementSpec.randomValue().getClass();
        GeneValue[] randomArray = (GeneValue[]) Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++) {
            randomArray[i] = elementSpec.randomValue();
        }
        return new ArrayGeneValue(randomArray);
    }
}
