package no.bols.w1.ai;//
//

import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public class NeuralBrain extends Brain {
    final BrainGeneWrapper genes;

    private Neuron singleNeuron = new Neuron(time);

    public NeuralBrain(Time time, GeneMap genes) {
        super(time);
        this.genes = new BrainGeneWrapper(genes);
        time.scheduleRecurringEvent(t -> singleNeuron.fireInputInverse(oneleg.getFoodDistanceOutput()), 10);
    }


}
