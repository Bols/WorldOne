package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;


/**
 * <h1>Må Oppnå</h1>
 * <li>resonans</li>
 * <li>distribuert vekt-justering</li>
 * <li>skalering/vekst/nye neuroner til å håndtere mer kompliserte sammenhenger</li>
 * <li>feedback til neuroner basert på om belønning var ihht. forventning?  belønning når en ny sammenheng er avdekket?</li>
 * <h1>Mulige mekanismer:</h1>
 * <li>stdp</li>
 * <li>dopamin - eksternt styrt? Fra neuronene selv? </li>
 * <li>programmert thalamus som innfører positiv feedback ved måloppnåelse</li>
 * <li>innføre nye neuroner - random?</li>
 * <li>gro nye synapser  - random?</li>
 * <li>utmatting av neuroner for å begrense aktivitet?</li>
 **/
public class NeuralBrain extends Brain {
    @Getter
    final BrainGene genes;
    private final Neuron foodDistanceInput;
    private final Neuron foodSensorInput;
    private final Neuron motorOutput;
    private final NeuronSpace neuronSpace;


    public NeuralBrain(Time time, BrainGene genes) {
        super(time);
        this.genes = genes;
        neuronSpace = new NeuronSpace(time, genes);
        foodDistanceInput = new Neuron(time, genes);
        foodSensorInput = new Neuron(time, genes);
        motorOutput = new Neuron(time, genes);
        neuronSpace.add(foodDistanceInput);
        neuronSpace.add(foodSensorInput);
        neuronSpace.add(motorOutput);
        neuronSpace.connectAll();
        foodDistanceInput.addProportionalInputTimeEvent(() -> oneleg.getFoodProximityOutput());
        motorOutput.addProportionalOutputTimeEvent(o -> oneleg.motorOutput(o));
        foodSensorInput.addProportionalInputTimeEvent(() -> oneleg.getEatingOutput());     // Kan erstattes med events ( istedet for recurring time event?)
        //neuronSpace.addProportionalDopamineTimeEvent(()->oneleg.getEatingOutput());


    }


}
