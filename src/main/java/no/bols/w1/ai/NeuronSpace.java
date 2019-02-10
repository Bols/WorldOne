package no.bols.w1.ai;//
//

import no.bols.w1.ai.neuron.Neuron;
import no.bols.w1.ai.neuron.NeuronTrait;
import no.bols.w1.physics.Time;

import java.util.HashSet;
import java.util.Set;

public class NeuronSpace {
    private final Class<? extends NeuronTrait>[] traits;
    private BrainGene genes;
    private Time time;
    private Set<Neuron> neurons = new HashSet<>();

    public NeuronSpace(Time time, BrainGene genes, Class<? extends NeuronTrait>... traits) {
        this.genes = genes;
        this.time = time;
        this.traits = traits;
    }

    public Neuron createNeuron() {
        Neuron n = new Neuron(time, genes, traits);
        neurons.add(n);
        return n;
    }

    public void connectAll() {
        for (Neuron neuron : neurons) {
            for (Neuron source : neurons) {
                if (neuron != source) {
                    neuron.addIncomingSynapticConnection(source);
                }

            }

        }

    }


   /* public void addProportionalDopamineTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(
                   
        );
    }*/
}
