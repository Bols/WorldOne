package no.bols.w1.genes.internal;//
//

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GeneArrayValue<T extends GeneValue> extends GeneValue<T[]> {
    private final T[] value;

    public GeneArrayValue(T[] value) {
        this.value = value;
    }

    @Override
    public GeneArrayValue<T> breed(GeneValue other, double mutationChance) {
        T[] result = value.clone();
        for (int i = 0; i < value.length; i++) {
            T otherValue = ((GeneArrayValue<T>) other).getValue()[i];
            result[i] = (T) value[i].breed(otherValue, mutationChance);
        }
        return new GeneArrayValue<>(result);

    }

    @Override
    public T[] getValue() {
        return value;
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
}
