package no.bols.w1.ai.neuron;//
//

import lombok.Data;

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
}
