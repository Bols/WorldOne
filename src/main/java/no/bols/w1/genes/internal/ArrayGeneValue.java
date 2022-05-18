package no.bols.w1.genes.internal;//
//

import javafx.util.Pair;
import no.bols.w1.genes.GeneScore;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayGeneValue<T extends GeneValue> extends GeneValue {
    private final T[] value;

    public ArrayGeneValue(T[] value) {
        this.value = value;
    }

    @Override
    public ArrayGeneValue<T> breed(GeneValue other, double mutationChance) {
        T[] result = value.clone();
        for (int i = 0; i < value.length; i++) {
            T otherValue = ((ArrayGeneValue<T>) other).getValue()[i];
            result[i] = (T) value[i].breed(otherValue, mutationChance);
        }
        return new ArrayGeneValue<>(result);

    }

    @Override
    public T[] getValue() {
        return value;
    }

    public ArrayGeneValue<T> clone() {
        return new ArrayGeneValue<>(value.clone());
    }

    @Override
    public void assignToField(Field field, Object geneInstance) {
        Class<?> elementClass = field.getType().getComponentType();
        Object valueArray = Array.newInstance(elementClass, value.length);
        for (int i = 0; i < value.length; i++) {
            Array.set(valueArray, i, value[i].getValue());
        }
        try {
            field.set(geneInstance, valueArray);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return "[" + Arrays.asList(value).stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(","))
                + "]";
    }


    @Override
    public GeneValue nextIncrementalValueForGradientDescent(double presentScore, double gamma, Function<GeneValue, Pair<? extends GeneScore, GeneMap>> simulateChangedValue) {
        if (!(value[0] instanceof DoubleGeneValue)) {
            return this;
        }
        List<GeneValue> valueList = Arrays.asList(value);
        Stream<GeneValue> nextIncrementalValue = valueList.stream()
                .parallel()
                .map(v -> v.nextIncrementalValueForGradientDescent(presentScore, gamma, changedDouble -> {
                    GeneValue[] changedArray = value.clone();
                            changedArray[valueList.indexOf(v)] = changedDouble;
                            return simulateChangedValue.apply(new ArrayGeneValue(changedArray));
                        })
                );
        return new ArrayGeneValue<>(nextIncrementalValue.collect(Collectors.toList()).toArray(value.clone()));
    }
}
