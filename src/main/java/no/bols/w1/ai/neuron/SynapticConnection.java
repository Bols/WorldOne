package no.bols.w1.ai.neuron;//
//

import lombok.Data;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.TimeValue;

import static no.bols.w1.ai.neuron.NeuronUtil.normalizeValue;

@Data
public class SynapticConnection {
    private Neuron source;
    private Neuron target;
    private double weight;
    private TimeValue lastWeightChange = TimeValue.none;
    private TimeValue lastBoost = TimeValue.none;

    public SynapticConnection(Neuron target, Neuron source) {
        this.source = source;
        source.addOutgoingSynapticConnection(this);
        this.target = target;
        this.weight = source.getInitialSynapseWeight();
    }

    public void changeWeight(double factor, Time.Instant time) {
        weight = normalizeValue(weight + weight * factor);
        lastWeightChange = new TimeValue(time, factor);

    }

    public void fire() {
        target.updateVoltagePotential(weight);
    }

    public void dopamineBoost(double boost, Time.Instant time) {
        weight = normalizeValue(weight + weight * boost);
        lastBoost = new TimeValue(time, boost);
    }
}
