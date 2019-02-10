package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.FireEvent;
import no.bols.w1.ai.SynapticConnection;

public class LinearSTDPSynapseTrait extends NeuronTrait {
    public LinearSTDPSynapseTrait(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void onPreSynapticSourceFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        BrainGene genes = target.getGenes();
        double weight = connection.getWeight();
        long timeDiff = fireEvent.getTime().timeSince(target.getLastFireTime());
        if (timeDiff < target.getGenes().getStdpPostTime() && timeDiff > 0) {
            connection.setWeight(weight - (weight * genes.getStdpFactor() * (genes.getStdpPostTime() - timeDiff) / genes.getStdpPostTime())); //linear for now
        }
        target.updateVoltagePotential(weight);
    }

    @Override
    public void onPostSynapticTargetFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        double weight = connection.getWeight();
        long timeDiff = fireEvent.getTime().timeSince(connection.getSource().getLastFireTime());
        if (timeDiff < target.getGenes().getStdpPreTime() && timeDiff > 0) {
            connection.setWeight(weight + (weight * target.getGenes().getStdpFactor() * (target.getGenes().getStdpPreTime() - timeDiff) / target.getGenes().getStdpPreTime())); //linear for now
        }
    }
}
