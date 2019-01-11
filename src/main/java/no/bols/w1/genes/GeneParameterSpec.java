package no.bols.w1.genes;//
//

import lombok.Getter;

import java.util.Random;


public class GeneParameterSpec extends GeneSpec<GeneParameterValue> {

    @Getter
    private final double min;
    @Getter
    private final double max;

    public GeneParameterSpec(double min, double max) {
        this.min = min;
        this.max = max;
    }


    public GeneParameterValue randomValue() {
        return new GeneParameterValue(this, new Random().nextDouble() * (max - min) + min);

    }
}
