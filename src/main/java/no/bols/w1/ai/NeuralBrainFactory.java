package no.bols.w1.ai;//
//

import no.bols.w1.BrainFactory;
import no.bols.w1.genes.GeneMap;
import no.bols.w1.genes.GeneParameterSpec;
import no.bols.w1.genes.GeneSpec;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;

import java.util.HashMap;
import java.util.Map;

public class NeuralBrainFactory implements BrainFactory {

    @Override
    public Brain createBrain(Time time, GeneMap genes) {
        return new NeuralBrain(time, genes);
    }

    @Override
    public Map<String, GeneSpec> geneSpec() {
        Map<String, GeneSpec> geneSpecs = new HashMap<>();
        geneSpecs.put(BrainGeneWrapper.EXHIBITION_FACTOR, new GeneParameterSpec(0, 1));
        geneSpecs.put(BrainGeneWrapper.LEAK_PER_MS, new GeneParameterSpec(0, 0.1));
        geneSpecs.put(BrainGeneWrapper.FIRE_TRESHOLD, new GeneParameterSpec(0.5, 1.0));
        return geneSpecs;
    }
}

