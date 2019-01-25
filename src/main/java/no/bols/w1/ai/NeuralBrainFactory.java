package no.bols.w1.ai;//
//

import no.bols.w1.BrainFactory;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public class NeuralBrainFactory implements BrainFactory<BrainGene> {

    @Override
    public Brain createBrain(Time time, BrainGene genes) {
        return new NeuralBrain(time, genes);
    }

    @Override
    public BrainGene geneSpec() {
        return new BrainGene();
    }


}

