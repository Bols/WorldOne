package no.bols.w1.genes;//
//

import lombok.EqualsAndHashCode;

import java.util.Random;

@EqualsAndHashCode
public class GeneParameterSpec extends GeneSpec {


    private final double min;
    private final double max;

    public GeneParameterSpec(double min, double max) {
        this.min = min;
        this.max = max;
    }


    public Gene randomValue() {
        return new GeneParameterValue(min, max, new Random().nextDouble() * (max - min) + min);

    }
}
