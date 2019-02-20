package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.physics.Time;

public class InhibitoryNeuron extends Neuron {
    public InhibitoryNeuron(Time time, BrainGene genes, Class<? extends NeuronTrait>[] traits) {
        super(time, genes, traits);
    }

    @Override
    public boolean isExcitatory() {
        return false;
    }
}
