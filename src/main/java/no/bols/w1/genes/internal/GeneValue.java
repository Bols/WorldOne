package no.bols.w1.genes.internal;//
//

import javafx.util.Pair;
import no.bols.w1.genes.GeneScore;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.function.Function;

public abstract class GeneValue {
    //protected GeneSpec<GeneValue<T>> geneSpec;
    //TODO: legg inn


    protected static Random random = new Random();

    public abstract GeneValue breed(GeneValue other, double mutationChance);

    protected boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    public abstract Object getValue();


    public void assignToField(Field field, Object geneInstance) {
        try {
            field.set(geneInstance, getValue());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public GeneValue nextIncrementalValueForGradientDescent(double presentScore, double gamma, Function<GeneValue, Pair<? extends GeneScore, GeneMap>> simulateChangedValue) {
        return this;
    }

}


