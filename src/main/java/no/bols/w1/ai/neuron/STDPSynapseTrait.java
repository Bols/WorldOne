package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.FireEvent;

public class STDPSynapseTrait extends NeuronTrait {
    public STDPSynapseTrait(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void onPreSynapticSourceFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        BrainGene genes = target.getGenes();
        long timeDiff = fireEvent.getTime().timeSince(target.getLastFireTime());
        if (timeDiff > 0) {
            double factor = genes.getStdpPreHalfTime() / (genes.getStdpPreHalfTime() + timeDiff);
            connection.changeWeight(-factor);
        }
    }

    @Override
    public void onPostSynapticTargetFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        long timeDiff = fireEvent.getTime().timeSince(connection.getSource().getLastFireTime());
        if (timeDiff > 0) {
            double factor = target.getGenes().getStdpPreHalfTime() / (target.getGenes().getStdpPreHalfTime() + timeDiff);
            connection.changeWeight(factor);
        }
    }

}
