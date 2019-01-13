package no.bols.w1.ai;//
//

import no.bols.w1.physics.Time;

import java.util.HashSet;
import java.util.Set;

public class NeuronSpace {
    private BrainGeneWrapper genes;
    private Time time;
    private Set<Neuron> neurons = new HashSet<>();

    public NeuronSpace(Time time, BrainGeneWrapper genes) {
        this.genes = genes;
        this.time = time;
    }

    public void add(Neuron neuron) {
        neurons.add(neuron);
    }

    public void connectAll() {
        for (Neuron neuron : neurons) {
            for (Neuron source : neurons) {
                if (neuron != source) {
                    neuron.addSynapticSource(source);
                }

            }

        }

    }


   /* public void addProportionalDopamineTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(
                   
        );
    }*/
}
