package no.bols.w1.genes.internal;//
//

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class GeneSpec<T extends GeneValue> {
    public GeneSpec(Annotation annotation, Field field) {
    }

    public abstract T randomValue();
}


