package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Neuron {
    private double voltage_state = 0;
    private long lastFireTime = 0;
    private Time time;
    private BrainGeneWrapper genes;
    private long lastUpdateState;
    private Set<SynapticSource> synapticConnections = new HashSet<>();
    private Set<Consumer<FireEvent>> fireListeners = new HashSet<>();

    public Neuron(Time time, BrainGeneWrapper genes) {
        this.time = time;
        this.genes = genes;
    }

    private void inputChange(double value) {
        double normalized_val = Math.min(1.0, Math.max(value, 0.0));
        updateState();
        voltage_state += normalized_val * genes.getExhibitionFactor();
        if (voltage_state > genes.getFireTreshold()) {
            voltage_state = 0; //TODO
            time.scheduleEvent(e -> fire(), 10);
        }
    }

    private void fire() {
        FireEvent fireEvent = new FireEvent(time.getTimeMilliSeconds(), this);
        lastFireTime = time.getTimeMilliSeconds();
        for (Consumer<FireEvent> fireListener : fireListeners) {
            fireListener.accept(fireEvent);
        }
    }


    private void updateState() {
        long now = time.getTimeMilliSeconds();
        voltage_state = voltage_state * (1 - (now - lastUpdateState) * genes.getLeakPerMs());
        lastUpdateState = now;
    }

    public void addProportionalOutputTimeEvent(Consumer<Double> output) {
        time.scheduleRecurringEvent(t -> {
            boolean firedRecently = t.getTimeMilliSeconds() - lastFireTime < 50;
            output.accept(firedRecently ? 1.0 : 0.0);
        }, 10);
    }

    public void addProportionalInputTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(t -> {
            this.inputChange(input.get());
        }, 10);
    }

    public void addSynapticSource(Neuron source) {
        synapticConnections.add(new SynapticSource(source));
    }

    public void addFireListener(Consumer<FireEvent> listener) {
        fireListeners.add(listener);
    }


    public class SynapticSource {
        private Neuron source;
        private double weight = .5;

        public SynapticSource(Neuron source) {
            this.source = source;
            source.addFireListener(fireEvent -> sourceFired(fireEvent));
        }

        private void sourceFired(FireEvent fireEvent) {
            Neuron.this.inputChange(weight);
        }
    }
}
