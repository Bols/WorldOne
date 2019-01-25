package no.bols.w1;//
//

import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public interface BrainFactory<T> {

    abstract Brain createBrain(Time time, T genes);

    T geneSpec();


}
