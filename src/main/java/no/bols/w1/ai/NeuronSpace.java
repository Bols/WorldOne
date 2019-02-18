package no.bols.w1.ai;//
//

import no.bols.w1.ai.neuron.InhibitoryNeuron;
import no.bols.w1.ai.neuron.Neuron;
import no.bols.w1.ai.neuron.NeuronTrait;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.TimeValue;

import java.util.HashSet;
import java.util.Set;

public class NeuronSpace {
    private final Class<? extends NeuronTrait>[] traits;
    private BrainGene genes;
    private Time time;
    private Set<Neuron> neurons = new HashSet<>();
    private TimeValue previousDopamineLevel = TimeValue.none;

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

    public Neuron createInhibitoryNeuron() {
        Neuron n = new InhibitoryNeuron(time, genes, traits);
        neurons.add(n);
        return n;
    }

    public void dopamineLevel(Double d) {
        TimeValue dopamineLevel = new TimeValue(time.getSimulatedTime(), d);
        if (d != previousDopamineLevel.getValue()) {
            //Må få raskere kjøretid på dette senere
            for (Neuron neuron : neurons) {
                neuron.differentialDopamineLevel(dopamineLevel, previousDopamineLevel);
            }
        }
        previousDopamineLevel = dopamineLevel;
    }


   /* public void addProportionalDopamineTimeEvent(Supplier<Double> input) {
        time.scheduleRecurringEvent(
                   
        );
    }*/
}
