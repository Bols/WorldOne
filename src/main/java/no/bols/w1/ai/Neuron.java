package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Neuron {
    private double voltage_state = 0;

    private Time time;
    private BrainGeneWrapper genes;
    private long lastUpdateState;

    public Neuron(Time time, BrainGeneWrapper genes) {
        this.time = time;
        this.genes = genes;
    }

    public void fireInput(double value) {
        double normalized_val = Math.min(1.0, Math.max(value, 0.0));
        updateState();
        voltage_state += normalized_val * genes.getExhibitionFactor();
        if (voltage_state > genes.getFireTreshold()) {

            // time.scheduleEvent(e -> onFire(), 10);
        }
    }


    private void updateState() {
        long now = time.getTimeMilliSeconds();
        voltage_state = voltage_state * (1 - (now - lastUpdateState) * genes.getLeakPerMs());
        lastUpdateState = now;
    }

    public void addProportionalOutput(Consumer<Double> output) {

    }

    public void addReverseProportionalInput(Supplier<Double> input) {
        time.scheduleRecurringEvent(t -> {
            double inverseValue = input.get();
            if (inverseValue <= 0) {
                fireInput(1.0);
            }
            this.fireInput(1.0 / inverseValue);
        }, 10);
    }
}
