package no.bols.w1.ai;//
//

import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public class NeuralBrain extends Brain {
    final BrainGeneWrapper genes;
    private final Neuron foodDistanceInput;
    private final Neuron foodSensorInput;
    private final Neuron motorOutput;
    private final NeuronSpace neuronSpace;


    public NeuralBrain(Time time, GeneMap geneMap) {
        super(time);
        this.genes = new BrainGeneWrapper(geneMap);
        neuronSpace = new NeuronSpace(time, genes);
        foodDistanceInput = new Neuron(time, genes);
        foodSensorInput = new Neuron(time, genes);
        motorOutput = new Neuron(time, genes);
        neuronSpace.add(foodDistanceInput);
        neuronSpace.add(foodSensorInput);
        neuronSpace.add(motorOutput);
        neuronSpace.connectAll();
    }


    @Override
    public void initializeRecurringInputEvents() {
        foodDistanceInput.addProportionalInputTimeEvent(() -> oneleg.getFoodProximityOutput());
        motorOutput.addProportionalOutputTimeEvent(o -> oneleg.motorOutput(o));
        foodSensorInput.addProportionalInputTimeEvent(() -> oneleg.getEatingOutput());     // Kan erstattes med events ( istedet for recurring time event?)
        //neuronSpace.addProportionalDopamineTimeEvent(()->oneleg.getEatingOutput());

        //time.scheduleRecurringEvent(t-> foodSensorInput.fireInput(oneleg.getEatingOutput()),10);
    }


    //resonans?
    //stdp
    //dopamin - eksternt styrt i første omgang, deretter fra neuroner basert på forventning?  dopamin når en ny sammenheng er avdekket?
}
