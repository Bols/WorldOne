package no.bols.w1.ai.neuron;//
//

import lombok.Data;

import static no.bols.w1.ai.neuron.NeuronUtil.normalizeValue;

@Data
public class SynapticConnection {
    private Neuron source;
    private Neuron target;
    private double weight = .5;

    public SynapticConnection(Neuron target, Neuron source) {
        this.source = source;
        source.addOutgoingSynapticConnection(this);
        this.target = target;
    }

    public void changeWeight(double factor) {
        weight = normalizeValue(weight + weight * factor);

    }

    public void fire() {
        target.updateVoltagePotential(weight);
    }
}
