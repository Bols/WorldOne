package no.bols.w1.genes.internal;//
//

import lombok.Getter;

import java.lang.reflect.Array;

public class GeneArraySpec<T extends GeneSpec> extends GeneSpec<GeneArrayValue> {
    @Getter
    private final int length;
    @Getter
    private final GeneSpec elementSpec;

    public GeneArraySpec(int length, GeneSpec elementSpec) {
        this.length = length;
        this.elementSpec = elementSpec;
    }


    @Override
    public GeneArrayValue randomValue() {
        Class<? extends GeneValue> elementType = elementSpec.randomValue().getClass();
        GeneValue[] randomArray = (GeneValue[]) Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++) {
            randomArray[i] = elementSpec.randomValue();
        }
        return new GeneArrayValue(randomArray);
    }
}
