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


    public NeuralBrain(Time time, GeneMap geneMap) {
        super(time);
        this.genes = new BrainGeneWrapper(geneMap);
        foodDistanceInput = new Neuron(time, genes);
        foodSensorInput = new Neuron(time, genes);
        motorOutput = new Neuron(time, genes);

        motorOutput.addProportionalOutput(o -> oneleg.motorOutput(o));
    }


    @Override
    public void initializeRecurringInputEvents() {
        foodDistanceInput.addReverseProportionalInput(() -> oneleg.getFoodDistanceOutput());

        //time.scheduleRecurringEvent(t-> foodSensorInput.fireInput(oneleg.getEatingOutput()),10);
    }

    //reward feedback
    // 
}
