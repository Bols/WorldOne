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
    private BrainGene genes;
    private long lastUpdateState;
    private Set<SynapticConnection> synapticConnections = new HashSet<>();
    private Set<Consumer<FireEvent>> fireListeners = new HashSet<>();

    public Neuron(Time time, BrainGene genes) {
        this.time = time;
        this.genes = genes;
    }

    private void inputChange(double value) {
        updateStateToNow();
        voltage_state += normalizeValue(value) * genes.getExhibitionFactor();
        if (voltage_state > genes.getFireTreshold()) {
            voltage_state = genes.getShortTimeDepression();
            time.scheduleEvent(e -> fire(), 1);
        }
    }

    private double normalizeValue(double value) {
        return Math.min(1.0, Math.max(value, 0.0));
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


    private void updateStateToNow() {
        long now = time.getTimeMilliSeconds();
        voltage_state = voltage_state - voltage_state * (now - lastUpdateState) * genes.getLeakPerMs();
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


    public class SynapticConnection {
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
            if (timeDiff < genes.getStdpPostTime() && timeDiff > 0) {
                weight = weight - (weight * genes.getStdpFactor() * genes.getStdpPostTime() / timeDiff); //linear for now
            }
            target.inputChange(weight);

        }

        public void targetNeuronFired(FireEvent fireEvent) {
            long timeDiff = fireEvent.getTime() - source.lastFireTime;
            if (timeDiff < genes.getStdpPreTime() && timeDiff > 0) {
                weight = weight + (weight * genes.getStdpFactor() * genes.getStdpPreTime() / timeDiff); //linear for now
            }
        }
    }
}
