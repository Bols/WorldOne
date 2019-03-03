package no.bols.w1.ai.neuron;//
//

import no.bols.w1.ai.BrainGene;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.TimeValue;

public class SimpleLeakyIntegratorTrait extends NeuronTrait {
    private double adaptiveFireTreshold;
    TimeValue voltage_state = TimeValue.none;
    public SimpleLeakyIntegratorTrait(Neuron neuron) {
        super(neuron);
        adaptiveFireTreshold = neuron.getGenes().getFireTreshold();
        voltage_state = new TimeValue(neuron.time.getSimulatedTime(), 0);
    }

    @Override
    public void updateState(Time.Instant now) {
        double voltage = this.voltage_state.getValue();
        this.voltage_state = new TimeValue(now, voltage - voltage * (now.timeSince(this.voltage_state.getValueInstant())) * neuron.getGenes().getLeakPerMs());
        //adaptiveFireTreshold=
    }

    @Override
    public void updateVoltagePotential(Time.Instant t, double value) {
        double voltage = voltage_state.getValue();
        voltage += value * neuron.genes.getExhibitionFactor() * refractoryPeriodFactor();
        BrainGene genes = neuron.getGenes();
        Time time = neuron.time;
        if (voltage > adaptiveFireTreshold) {
            neuron.lastFireTime = time.getSimulatedTime();
            voltage_state = new TimeValue(t, genes.getShortTimeDepression());
            time.scheduleEvent(e -> neuron.fire(), 1);
        } else {
            voltage_state = new TimeValue(t, voltage);
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
