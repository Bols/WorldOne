package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.FireEvent;
import no.bols.w1.physics.Time;

public abstract class NeuronTrait {

    protected Neuron neuron;

    public NeuronTrait(Neuron neuron) {
        this.neuron = neuron;
    }

    public void updateState(Time.Instant lastUpdateState) {
    }

    public void onFire(FireEvent f) {
    }

    public void onPreSynapticSourceFired(FireEvent fireEvent, SynapticConnection connection) {

    }

    public void onPostSynapticTargetFired(FireEvent event, SynapticConnection connection) {

    }


    public void updateVoltagePotential(double value) {
    }

    protected double normalizeValue(double value) {
        return Math.min(1.0, Math.max(value, 0.0));
    }

}