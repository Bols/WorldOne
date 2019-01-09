package no.bols.w1.ai;//
//

import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public class NeuralBrain extends Brain {
    final BrainGeneWrapper genes;
    private final Neuron singleNeuron;


    public NeuralBrain(Time time, GeneMap geneMap) {
        super(time);
        this.genes = new BrainGeneWrapper(geneMap);
        singleNeuron = new Neuron(time, genes);
        time.scheduleRecurringEvent(t -> singleNeuron.fireInputInverse(oneleg.getFoodDistanceOutput()), 10);

    }


}
