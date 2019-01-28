package no.bols.w1.genes.internal;//
//

import java.lang.reflect.Field;
import java.util.Random;

public abstract class GeneValue<T> {

    protected static Random random = new Random();

    public abstract GeneValue<T> breed(GeneValue<T> other, double mutationChance);

    protected boolean chance(double percent) {
        return random.nextDouble() < percent;
    }

    public abstract T getValue();


    public void assignToField(Field field, Object geneInstance) {
        try {
            field.set(geneInstance, getValue());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}


