package no.bols.w1.genes.internal;//
//

import java.lang.reflect.Field;

public abstract class GeneValue<T> {

    public abstract GeneValue<T> breed(GeneValue<T> other, double mutationChance);

    public abstract T getValue();


    public void assignToField(Field field, Object geneInstance) {
        try {
            field.set(geneInstance, getValue());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}


