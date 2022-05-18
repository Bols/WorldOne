package no.bols.w1.genes.internal;//
//

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class GeneSpec<T extends GeneValue> {
    private final Annotation annotation;
    private final Field field;

    public GeneSpec(Annotation annotation, Field field) {
        this.annotation = annotation;
        this.field = field;
    }

    public abstract T randomValue();
}


