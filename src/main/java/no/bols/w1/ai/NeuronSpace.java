package no.bols.w1.ai;//
//

import java.util.HashSet;
import java.util.Set;

public class NeuronSpace {
    private BrainGeneWrapper genes;
    private Set<Neuron> neurons = new HashSet<>();

    public NeuronSpace(BrainGeneWrapper genes) {
        this.genes = genes;
    }

    public void add(Neuron neuron) {
        neurons.add(neuron);
    }

    public void initialize() {
        for (Neuron neuron : neurons) {
            for (Neuron source : neurons) {
                if (neuron != source) {
                    neuron.addSynapticSource(source);
                }

            }

        }

    }
}
