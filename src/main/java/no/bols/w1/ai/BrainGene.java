package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.DoubleGene;

public class BrainGene {

    @DoubleGene(min = .0, max = 1.0)
    @Getter
    public double exhibitionFactor;

    @DoubleGene(min = .0, max = 1.0)
    @Getter
    public double leakPerMs;

    @DoubleGene(min = .0, max = 1.0)
    @Getter
    public double fireTreshold;

    @Getter
    @DoubleGene(min = .0, max = 1.0)
    public double stdpFactor = 0.3;
}
