package no.bols.w1.ai.neuron;//
//

import lombok.Data;

import static no.bols.w1.ai.neuron.NeuronUtil.normalizeValue;

@Data
public class SynapticConnection {
    private Neuron source;
    private Neuron target;
    private double weight;

    public SynapticConnection(Neuron target, Neuron source) {
        this.source = source;
        source.addOutgoingSynapticConnection(this);
        this.target = target;
        this.weight = source.getInitialSynapseWeight();
    }

    public void changeWeight(double factor) {
        weight = normalizeValue(weight + weight * factor * source.getGenes().getStdpModificationSpeed());

    }

    public void fire() {
        target.updateVoltagePotential(weight);
    }
}
