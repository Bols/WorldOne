package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.ai.neuron.Neuron;
import no.bols.w1.ai.neuron.STDPSynapseTrait;
import no.bols.w1.ai.neuron.SimpleLeakyIntegratorTrait;
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
 * <li>gro nye synapser  - random?</li>
 * <li>utmatting av neuroner for å begrense aktivitet?</li>
 * <li>Innføre regioner med ulike regler for feedback-synapser og andre regler. Columns? </li>
 * <li>Innføre et "klokkesignal" som trigger bølger gjennom regioner - </li>
 **/
public class NeuralBrain extends Brain {
    @Getter
    final BrainGene genes;
    private final Neuron foodDistanceInput;
    private final Neuron foodSensorInput;
    private final Neuron motorOutput;
    private final NeuronSpace neuronSpace;
    private final Neuron eatingSensorInput;


    public NeuralBrain(Time time, BrainGene genes) {
        super(time);
        this.genes = genes;
        neuronSpace = new NeuronSpace(time, genes, STDPSynapseTrait.class, SimpleLeakyIntegratorTrait.class);
        foodDistanceInput = neuronSpace.createNeuron();
        foodSensorInput = neuronSpace.createNeuron();
        eatingSensorInput = neuronSpace.createNeuron();
        motorOutput = neuronSpace.createNeuron();
        Neuron hiddenExhibitoryNeuron = neuronSpace.createNeuron();
        Neuron hiddenInhibitoryNeuron = neuronSpace.createInhibitoryNeuron();

        foodDistanceInput.addOutgoingSynapticConnection(hiddenExhibitoryNeuron);
        foodSensorInput.addOutgoingSynapticConnection(hiddenInhibitoryNeuron);
        hiddenExhibitoryNeuron.addOutgoingSynapticConnection(motorOutput);
        hiddenInhibitoryNeuron.addOutgoingSynapticConnection(motorOutput);


        eatingSensorInput.addProportionalOutputTimeEvent(o -> neuronSpace.dopamineLevel(o));

        foodSensorInput.addProportionalInputTimeEvent(() -> oneleg.isInEatingDistance() ? 1 : 0.0);
        foodDistanceInput.addProportionalInputTimeEvent(() -> oneleg.getFoodProximityOutput());
        motorOutput.addProportionalOutputTimeEvent(o -> oneleg.motorOutput(o));
        foodSensorInput.addProportionalInputTimeEvent(() -> oneleg.getEatingOutput());     // Kan erstattes med events ( istedet for recurring time event?)
        //neuronSpace.addProportionalDopamineTimeEvent(()->oneleg.getEatingOutput());


    }


}

/*
Dendritiske trær er i seg selv ikke-lineære (tre eller flere signaler inn gir mer enn 3x effekten på nevronet)


Vekst:               https://www.ncbi.nlm.nih.gov/books/NBK234146/
Nevronal celledeling (symmetrisk og assymetrisk)
Retning på axoner - mesteparten nedover i cortex



 */