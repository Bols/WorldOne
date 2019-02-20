package no.bols.w1.ai.neuron;//
//

import lombok.Getter;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.FireEvent;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.TimeValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Neuron {
    double voltage_state = 0;
    @Getter
    Time.Instant lastFireTime;
    Time time;
    @Getter
    BrainGene genes;
    @Getter
    Time.Instant lastUpdateState;
    Set<SynapticConnection> incomingPostSynapticConnections = new HashSet<>();
    private Set<SynapticConnection> outgoingPreSynapticConnections = new HashSet<>();
    private List<NeuronTrait> neuronTraits = new ArrayList<>();

    public Neuron(Time time, BrainGene genes, Class<? extends NeuronTrait>[] traits) {
        this.time = time;
        this.genes = genes;
        for (Class<? extends NeuronTrait> trait : traits) {
            addTrait(trait);
        }
    }

    public Neuron addTrait(Class<? extends NeuronTrait> trait) {
        try {
            neuronTraits.add(trait.getConstructor(Neuron.class).newInstance(this));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    void updateVoltagePotential(double value) {
        updateStateToNow();
        for (NeuronTrait neuronTrait : neuronTraits) {
            neuronTrait.updateVoltagePotential(value);
        }
    }


    void fire() {
        FireEvent fireEvent = new FireEvent(time.getSimulatedTime(), this);
        time.addNeuronFireCountStat();
        for (SynapticConnection outgoingPreSynapticConnection : outgoingPreSynapticConnections) {
            outgoingPreSynapticConnection.fire();
            for (NeuronTrait neuronTrait : neuronTraits) {
                neuronTrait.onPreSynapticSourceFired(fireEvent, outgoingPreSynapticConnection);
            }
        }
        for (SynapticConnection incomingConnection : incomingPostSynapticConnections) {
            for (NeuronTrait neuronTrait : neuronTraits) {
                neuronTrait.onPostSynapticTargetFired(fireEvent, incomingConnection);
            }

        }
        for (NeuronTrait trait : neuronTraits) {
            trait.onFire(fireEvent);
        }
        lastFireTime = fireEvent.getTime();
    }


    private void updateStateToNow() {
        for (NeuronTrait trait : neuronTraits) {
            trait.updateState(lastUpdateState);
        }
        lastUpdateState = time.getSimulatedTime();

    }

    public void addProportionalOutputTimeEvent(Consumer<Double> output) {
        time.scheduleRecurringEvent(t -> {
            boolean firedRecently = lastFireTime != null && t.timeSince(lastFireTime) < 50;
            output.accept(firedRecently ? 1.0 : 0.0);
        }, 10);
    }

    public void addProportionalInputTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(t -> this.updateVoltagePotential(input.get()), 10);
    }

    void addOutgoingSynapticConnection(SynapticConnection synapticConnection) {
        outgoingPreSynapticConnections.add(synapticConnection);
    }


    public boolean isExcitatory() {
        return true;
    }


    public void addOutgoingSynapticConnection(Neuron target) {
        target.incomingPostSynapticConnections.add(new SynapticConnection(target, this));
    }


    public void differentialDopamineLevel(TimeValue dopamineLevel, TimeValue previousDopamineLevel) {
        for (SynapticConnection incomingPostSynapticConnection : incomingPostSynapticConnections) {    //Really must be faster
            for (NeuronTrait neuronTrait : neuronTraits) {
                neuronTrait.onDifferentialDopamineLevel(dopamineLevel, incomingPostSynapticConnection, previousDopamineLevel);
            }

        }

    }

    public double exhibitorySign() {
        return this.isExcitatory() ? 1 : -1;
    }
}
