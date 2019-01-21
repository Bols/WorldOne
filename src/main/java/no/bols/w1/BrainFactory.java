package no.bols.w1;//
//

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.util.Factory;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public interface BrainFactory {

    abstract Brain createBrain(Time time, Genotype<DoubleGene> genes);

    Factory<Genotype<DoubleGene>> genotypeFactory();


}
