package no.bols.w1.ai.neuron;

import junit.framework.TestCase;
import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.NeuronSpace;
import no.bols.w1.physics.Time;

//

public class STDPSynapseTraitTest extends TestCase {
    public void testPrePostAndDopamine() throws Exception {
        Time time = new Time();
        Time.BATCH_SIZE = 1;
        NeuronSpace ns = new NeuronSpace(time, new BrainGene(), new Class[]{SimpleLeakyIntegratorTrait.class, STDPSynapseTrait.class});
        Neuron source = ns.createNeuron();
        Neuron target = ns.createNeuron();
        SynapticConnection connection = new SynapticConnection(target, source);
        source.addOutgoingSynapticConnection(connection);
        target.incomingPostSynapticConnections.add(connection);
        double startState = target.voltage_state;
        double startWeight = connection.getWeight();
        time.runSingleEvent(t -> source.fire(), 100);
        assertEquals(startWeight, connection.getWeight());
        assertTrue(target.voltage_state > startState);
        time.runSingleEvent(t -> target.fire(), 3);
        double weightAfterPostFire = connection.getWeight();
        assertTrue(weightAfterPostFire > startWeight);
        time.runSingleEvent(t -> ns.dopamineLevel(0.5), 1000);

        time.runSingleEvent(t -> ns.dopamineLevel(1.0), 1000);
        double weightAfterDopamine = connection.getWeight();
        assertTrue(weightAfterDopamine > weightAfterPostFire);

    }

}