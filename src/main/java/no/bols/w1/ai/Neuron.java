package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

public class Neuron {
    private double voltage_state = 0;
    private Time time;

    public Neuron(Time time) {
        this.time = time;
    }

    public void fireInputInverse(double inverseValue) {
        if (inverseValue <= 0) {
            fireInput(1.0);
        }
        this.fireInput(1.0 / inverseValue);
    }

    private void fireInput(double value) {
        double normalized_val = Math.min(1.0, Math.max(value, 0.0));
    }
}
