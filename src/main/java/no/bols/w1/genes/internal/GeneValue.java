package no.bols.w1.genes.internal;//
//

import javafx.util.Pair;
import no.bols.w1.genes.GeneScore;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Function;

public abstract class GeneValue<T> {
    //protected GeneSpec<GeneValue<T>> geneSpec;
    //TODO: legg inn


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


    public GeneValue<T> nextIncrementalValueForGradientDescent(double presentScore, double gamma, Function<GeneValue<T>, Pair<GeneScore, GeneMap>> simulateChangedValue) {
        return this;
    }

}


