package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterValue;

public class BrainGeneWrapper {

    public static final String EXHIBITION_FACTOR = "exhibition";
    public static final String LEAK_PER_MS = "leak";
    public static final String FIRE_TRESHOLD = "leak";

    @Getter
    protected GeneMap geneMap;

    @Getter
    private double exhibitionFactor;
    @Getter
    private double leakPerMs;

    @Getter
    private double fireTreshold;

    public BrainGeneWrapper(GeneMap geneMap) {
        this.geneMap = geneMap;
        initializeConstants(geneMap);
    }

    protected void initializeConstants(GeneMap geneMap) {
        exhibitionFactor = ((GeneParameterValue) (geneMap.genes.get(BrainGeneWrapper.EXHIBITION_FACTOR))).getValue();
        leakPerMs = ((GeneParameterValue) (geneMap.genes.get(BrainGeneWrapper.LEAK_PER_MS))).getValue();
        fireTreshold = ((GeneParameterValue) (geneMap.genes.get(BrainGeneWrapper.FIRE_TRESHOLD))).getValue();
    }

    public GeneMap getGeneMap() {
        return this.geneMap;
    }
}
