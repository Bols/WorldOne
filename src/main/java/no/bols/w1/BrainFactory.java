package no.bols.w1;//
//

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public interface BrainFactory {

    abstract Brain createBrain(Time time, Genotype<DoubleGene> genes);

    GeneMap randomGenes();


}
