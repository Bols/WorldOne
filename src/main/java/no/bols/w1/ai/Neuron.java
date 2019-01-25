package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Neuron {
    public static long STDP_PRE_TIME = 100;
    public static long STDP_POST_TIME = 30;
    public static double STDP_FACTOR = .3;
    private double voltage_state = 0;
    private long lastFireTime = 0;
    private Time time;
    private BrainGene genes;
    private long lastUpdateState;
    private Set<SynapticConnection> synapticConnections = new HashSet<>();
    private Set<Consumer<FireEvent>> fireListeners = new HashSet<>();

    public Neuron(Time time, BrainGene genes) {
        this.time = time;
        this.genes = genes;
    }

    private void inputChange(double value) {
        double normalized_val = Math.min(1.0, Math.max(value, 0.0));
        updateState();
        voltage_state += normalized_val * genes.getExhibitionFactor();
        if (voltage_state > genes.getFireTreshold()) {
            voltage_state = 0; //TODO
            time.scheduleEvent(e -> fire(), 1);
        }
    }

    private void fire() {
        FireEvent fireEvent = new FireEvent(time.getTimeMilliSeconds(), this);
        lastFireTime = time.getTimeMilliSeconds();
        for (Consumer<FireEvent> fireListener : fireListeners) {
            fireListener.accept(fireEvent);
        }
        for (SynapticConnection synapticConnection : synapticConnections) {
            synapticConnection.targetNeuronFired(fireEvent);
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
        synapticConnections.add(new SynapticConnection(source, this));
    }

    public void addFireListener(Consumer<FireEvent> listener) {
        fireListeners.add(listener);
    }


    public static class SynapticConnection {
        private Neuron source;
        private Neuron target;
        private double weight = .5;

        public SynapticConnection(Neuron source, Neuron target) {
            this.source = source;
            this.target = target;
            source.addFireListener(fireEvent -> sourceNeuronFired(fireEvent));
        }

        private void sourceNeuronFired(FireEvent fireEvent) {
            long timeDiff = fireEvent.getTime() - target.lastFireTime;
            if (timeDiff < STDP_POST_TIME && timeDiff > 0) {
                weight = weight - (weight * STDP_FACTOR * STDP_POST_TIME / timeDiff); //linear for now
            }
            target.inputChange(weight);
        }

        public void targetNeuronFired(FireEvent fireEvent) {
            long timeDiff = fireEvent.getTime() - source.lastFireTime;
            if (timeDiff < STDP_PRE_TIME && timeDiff > 0) {
                weight = weight + (weight * STDP_FACTOR * STDP_PRE_TIME / timeDiff); //linear for now
            }
        }
    }
}
