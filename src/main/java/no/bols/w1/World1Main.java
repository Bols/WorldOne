package no.bols.w1;//
//

import no.bols.w1.ai.NeuralBrainFactory;

public class World1Main {

    public static void main(String[] args) {
        World1SimulatorRunner.builder()
                .scenarioTimeMs(10000)
                .brainFactory(new NeuralBrainFactory())
                .build().runGeneticAlgorithmUntilStable();
    }
}
