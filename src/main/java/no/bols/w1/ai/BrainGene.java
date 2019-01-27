package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.BooleanGene;
import no.bols.w1.genes.DoubleGene;

public class BrainGene {

    @DoubleGene(min = .0, max = .5)
    @Getter
    public double exhibitionFactor = 0.5;

    @DoubleGene(min = .0, max = .2)
    @Getter
    public double leakPerMs = 0.67;

    @DoubleGene(min = .5, max = 1.0)
    @Getter
    public double fireTreshold = 0.6;


    //Synapses

    @Getter
    //@DoubleGene(min = .0, max = 1.0)
    public double stdpFactor = .2;

    @Getter
    //@DoubleGene(min = .0, max = 200.0)
    public double stdpPreTime = 130;

    @Getter
    //@DoubleGene(min = .0, max = 100.0)
    public double stdpPostTime = 60;

    @BooleanGene()
    @Getter
    public boolean useStdp = true;
}
