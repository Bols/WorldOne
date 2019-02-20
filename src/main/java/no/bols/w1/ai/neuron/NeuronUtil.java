package no.bols.w1.ai.neuron;//
//

public class NeuronUtil {


    static double normalizeValue(double value) {
        return Math.min(1.0, Math.max(value, 0.0));
    }

}
