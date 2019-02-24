package no.bols.w1.ai;//
//

import lombok.Getter;
import no.bols.w1.genes.DoubleGene;

@Getter
public class BrainGene {

    @DoubleGene(min = .0, max = 1)
    public double exhibitionFactor = 0.5;

    @DoubleGene(min = .0, max = .001)
    public double leakPerMs = 0.0;

    @DoubleGene(min = .5, max = 1.0)
    public double fireTreshold = 0.6;

    @DoubleGene(min = -.20, max = -.05)
    public double shortTimeDepression = -.10;

    //Synapses

    @DoubleGene(min = .0, max = 0.1)
    public double stdpMobility = 0.05;

    @DoubleGene(min = 2, max = 20)
    public double stdpDopamineMobilityBoost = 3;

    @DoubleGene(min = 50, max = 3000)
    public double stdpDopamineEligibilityTimeout = 2000;

    @DoubleGene(min = 10, max = 30.0)
    public double stdpPreHalfTime = 20;

    @DoubleGene(min = 5, max = 20.0)
    public double stdpPostHalfTime = 10;


    @DoubleGene(min = .0, max = 0.2)
    public double initialSynapseWeight = .1;
}
