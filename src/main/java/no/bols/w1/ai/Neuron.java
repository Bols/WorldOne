package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Neuron {
    private double voltage_state = 0;
    private Time.Instant lastFireTime;
    private Time time;
    private BrainGene genes;
    private Time.Instant lastUpdateState;
    private Set<SynapticConnection> incomingConnections = new HashSet<>();
    private Set<Consumer<FireEvent>> fireListeners = new HashSet<>();

    public Neuron(Time time, BrainGene genes) {
        this.time = time;
        this.genes = genes;

    }

    private void voltageUpdate(double value) {
        updateStateToNow();
        voltage_state += normalizeValue(value) * genes.getExhibitionFactor() * refractoryPeriodFactor();
        if (voltage_state > genes.getFireTreshold()) {
            lastFireTime = time.getSimulatedTime();
            voltage_state = genes.getShortTimeDepression();
            time.scheduleEvent(e -> fire(), 1);
        }
    }

    private double refractoryPeriodFactor() {
        long timeSinceLastFire = time.timeSince(lastFireTime);
        if (timeSinceLastFire <= 10) {
            return 0;
        }
        return 1 - (10 / timeSinceLastFire);
    }

    private double normalizeValue(double value) {
        return Math.min(1.0, Math.max(value, 0.0));
    }

    private void fire() {
        FireEvent fireEvent = new FireEvent(time.getSimulatedTime(), this);
        time.addNeuronFireCountStat();
        for (Consumer<FireEvent> fireListener : fireListeners) {
            fireListener.accept(fireEvent);
        }
        for (SynapticConnection synapticConnection : incomingConnections) {
            synapticConnection.targetNeuronFired(fireEvent);
        }
    }


    private void updateStateToNow() {
        voltage_state = voltage_state - voltage_state * (time.timeSince(lastUpdateState)) * genes.getLeakPerMs();
        lastUpdateState = time.getSimulatedTime();
    }

    public void addProportionalOutputTimeEvent(Consumer<Double> output) {
        time.scheduleRecurringEvent(t -> {
            boolean firedRecently = lastFireTime != null && t.timeSince(lastFireTime) < 50;
            output.accept(firedRecently ? 1.0 : 0.0);
        }, 10);
    }

    public void addProportionalInputTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(t -> {
            this.voltageUpdate(input.get());
        }, 10);
    }

    public void addSynapticSource(Neuron source) {
        incomingConnections.add(new SynapticConnection(source, this));
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
            long timeDiff = fireEvent.getTime().timeSince(target.lastFireTime);
            if (timeDiff < genes.getStdpPostTime() && timeDiff > 0) {
                weight = weight - (weight * genes.getStdpFactor() * (genes.getStdpPostTime() - timeDiff) / genes.getStdpPostTime()); //linear for now
            }
            target.voltageUpdate(weight);

        }

        public void targetNeuronFired(FireEvent fireEvent) {
            long timeDiff = fireEvent.getTime().timeSince(source.lastFireTime);
            if (timeDiff < genes.getStdpPreTime() && timeDiff > 0) {
                weight = weight + (weight * genes.getStdpFactor() * (genes.getStdpPreTime() - timeDiff) / genes.getStdpPreTime()); //linear for now
            }
        }
    }
}
