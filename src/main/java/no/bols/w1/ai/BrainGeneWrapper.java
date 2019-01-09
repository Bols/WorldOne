package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterValue;

public class BrainGeneWrapper {

    public static final String EXHIBITION_FACTOR = "EXHIBITION_FACTOR";
    @Getter
    protected GeneMap geneMap;
    private float exhibitionFactor;

    public BrainGeneWrapper(GeneMap geneMap) {
        this.geneMap = geneMap;
        initializeConstants(geneMap);
    }

    protected void initializeConstants(GeneMap geneMap) {
        exhibitionFactor = ((GeneParameterValue) (geneMap.genes.get(BrainGeneWrapper.EXHIBITION_FACTOR))).getValue();
    }

    public GeneMap getGeneMap() {
        return this.geneMap;
    }
}
