package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.physics.Time;

public class SimpleLeakyIntegratorTrait extends NeuronTrait {
    public SimpleLeakyIntegratorTrait(Neuron neuron) {
        super(neuron);
    }

    @Override
    public void updateState(Time.Instant lastUpdateState) {
        double voltage_state = neuron.voltage_state;
        neuron.voltage_state = voltage_state - voltage_state * (neuron.time.timeSince(lastUpdateState)) * neuron.getGenes().getLeakPerMs();
    }

    @Override
    public void updateVoltagePotential(double value) {
        neuron.voltage_state += normalizeValue(value) * neuron.genes.getExhibitionFactor() * refractoryPeriodFactor();
        BrainGene genes = neuron.getGenes();
        Time time = neuron.time;
        if (neuron.voltage_state > genes.getFireTreshold()) {
            neuron.lastFireTime = time.getSimulatedTime();
            neuron.voltage_state = genes.getShortTimeDepression();
            time.scheduleEvent(e -> neuron.fire(), 1);
        }
    }

    private double refractoryPeriodFactor() {
        long timeSinceLastFire = neuron.time.timeSince(neuron.lastFireTime);
        if (timeSinceLastFire <= 10) {
            return 0;
        }
        return 1 - (10 / timeSinceLastFire);
    }
}
