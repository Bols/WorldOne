package no.bols.w1;

import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.Engine;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.List;


@Builder
public class World1SimulatorRunner<G> {
    private int scenarioTimeMs = 100000;
    BrainFactory<G> brainFactory;

    private WorldScoreWithTrainingHistory evaluate(G genes) {
        Time time = new Time();
        Brain brain = brainFactory.createBrain(time, genes);
        WorldScoreWithTrainingHistory score = runScenarioTrainingUntilStable(time, brain);
        return score;
    }

    private WorldScoreWithTrainingHistory runScenarioTrainingUntilStable(Time time, Brain brain) {
        WorldScoreWithTrainingHistory scoreList = new WorldScoreWithTrainingHistory();
        do {
            time.reset();
            World simulationWorld = new World(time, brain);
            time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            //System.out.println("New score "+simulationWorld.score()+" prev score "+previousScore);
            scoreList.addScore(simulationWorld.score());
        } while (scoreList.lastScoreWasImprovement());
        return scoreList;
    }


    public List<Pair<WorldScoreWithTrainingHistory, G>> runGeneticAlgorithmUntilStable() {
        return Engine.<G, WorldScoreWithTrainingHistory>builder()
                .initialPopulation(100)
                .generationUsableSize(20)
                .mutationChance(.2)
                .evalFunction(g -> this.evaluate(g))
                .gene(brainFactory.geneSpec())
                .build()
                .runGeneticAlgorithmUntilStable();
    }

}
