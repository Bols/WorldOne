package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.ai.FireEvent;
import no.bols.w1.physics.TimeValue;

public class STDPSynapseTrait extends NeuronTrait {
    public STDPSynapseTrait(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void onPreSynapticSourceFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        BrainGene genes = target.getGenes();
        long timeDiff = fireEvent.getTime().timeSince(target.getLastFireTime());
        if (timeDiff > 0 && timeDiff < genes.getStdpPreHalfTime() * 32) {
            double factor = genes.getStdpPreHalfTime() / (genes.getStdpPreHalfTime() + timeDiff);
            connection.changeWeight(-factor * fireEvent.getSource().getGenes().getStdpMobility(), fireEvent.getTime());
        }
    }

    @Override
    public void onPostSynapticTargetFired(FireEvent fireEvent, SynapticConnection connection) {
        Neuron target = connection.getTarget();
        long timeDiff = fireEvent.getTime().timeSince(connection.getSource().getLastFireTime());
        if (timeDiff > 0) {
            double factor = target.getGenes().getStdpPostHalfTime() / (target.getGenes().getStdpPostHalfTime() + timeDiff);
            connection.changeWeight(factor * fireEvent.getSource().getGenes().getStdpMobility(), fireEvent.getTime());
        }
    }

    @Override
    public void onDifferentialDopamineLevel(TimeValue dopamineLevel, SynapticConnection synapse, TimeValue previousDopamineLevel) {
        if (neuron.isExcitatory()) {
            double eligibility = synapse.getLastWeightChange().linearDecay(dopamineLevel.getValueInstant(), neuron.getGenes().getStdpDopamineEligibilityTimeout());
            double timeSlicedEligibility = dopamineLevel.timeSliceValueSince(previousDopamineLevel) * eligibility;
            synapse.dopamineBoost(timeSlicedEligibility * neuron.getGenes().getStdpDopamineMobilityBoost(), dopamineLevel.getValueInstant());
        }

    }


}
