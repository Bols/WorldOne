package no.bols.w1.ai.neuron;//
//

public class InhibitoryNeuronTrait extends NeuronTrait {
    public InhibitoryNeuronTrait(Neuron neuron) {
        super(neuron);
        neuron.setInitialSynapseWeight(-neuron.getGenes().getInitialSynapseWeight());
    }
}
