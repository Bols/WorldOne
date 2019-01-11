package no.bols.w1.genes;//
//

import no.bols.w1.ai.BrainGeneWrapper;

public class GeneFactory {
    public GeneMap initGenes() {
        GeneMap geneMap = new GeneMap();
        geneMap.genes.put(BrainGeneWrapper.EXHIBITION_FACTOR, new GeneParameterValue(0, 1));
        geneMap.genes.put(BrainGeneWrapper.LEAK_PER_MS, new GeneParameterValue(0, 0.1));
        geneMap.genes.put(BrainGeneWrapper.FIRE_TRESHOLD, new GeneParameterValue(0.5, 1.0));

        return geneMap;
    }
}
