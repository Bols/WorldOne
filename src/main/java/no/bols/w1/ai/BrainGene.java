package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.BooleanGene;
import no.bols.w1.genes.DoubleGene;

@Getter
public class BrainGene {

    @DoubleGene(min = .0, max = .5)
    public double exhibitionFactor = 0.5;

    @DoubleGene(min = .0, max = .2)
    public double leakPerMs = 0.67;

    @DoubleGene(min = .5, max = 1.0)
    public double fireTreshold = 0.6;

    @DoubleGene(min = -.20, max = -.5)
    public double shortTimeDepression;

    //Synapses

    @DoubleGene(min = .0, max = 1.0)
    public double stdpFactor = .2;

    @DoubleGene(min = .0, max = 200.0)
    public double stdpPreTime = 130;

    @DoubleGene(min = .0, max = 100.0)
    public double stdpPostTime = 60;


}
