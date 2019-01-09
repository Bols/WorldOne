package no.bols.w1.ai;//
//

import no.bols.w1.BrainFactory;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterValue;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

public class NeuralBrainFactory implements BrainFactory {

    @Override
    public Brain createBrain(Time time, GeneMap genes) {
        return new NeuralBrain(time, genes);
    }

    @Override
    public GeneMap randomGenes() {
        GeneMap geneMap = new GeneMap();
        geneMap.genes.put(BrainGeneWrapper.EXHIBITION_FACTOR, new GeneParameterValue(0, 1));
        geneMap.genes.put(BrainGeneWrapper.LEAK_PER_MS, new GeneParameterValue(0, 0.1));
        geneMap.genes.put(BrainGeneWrapper.FIRE_TRESHOLD, new GeneParameterValue(0.5, 1.0));

        return geneMap;
    }

}
