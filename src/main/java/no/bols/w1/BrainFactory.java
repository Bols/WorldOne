package no.bols.w1;//
//

import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneSpec;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.Map;

public interface BrainFactory {

    abstract Brain createBrain(Time time, GeneMap genes);

    Map<String, GeneSpec> geneSpec();


}
