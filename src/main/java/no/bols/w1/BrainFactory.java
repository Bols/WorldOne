package no.bols.w1;//
//

import no.bols.w1.genes.GeneMap;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public interface BrainFactory {

    abstract Brain createBrain(Time time, GeneMap genes);

    GeneMap randomGenes();


}
