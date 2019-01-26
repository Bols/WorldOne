package no.bols.w1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.Engine;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.JfxVisualize;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.List;


@Builder
public class World1SimulatorRunner<G> {
    private int scenarioTimeMs = 100000;
    BrainFactory<G> brainFactory;
    private JfxVisualize jfxVisualize;
    private Pair<WorldScoreWithTrainingHistory, G> bestScore;

    private WorldScoreWithTrainingHistory evaluate(G genes) {
        Time time = new Time();
        Brain brain = brainFactory.createBrain(time, genes);
        WorldScoreWithTrainingHistory score = runScenarioTrainingUntilStable(time, brain);
        return score;
    }

    private WorldScoreWithTrainingHistory runScenarioTrainingUntilStable(Time time, Brain brain) {
        WorldScoreWithTrainingHistory scoreList = new WorldScoreWithTrainingHistory(time, brain);
        do {
            time.reset();
            World simulationWorld = new World(time, brain);
            time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            scoreList.addScore(simulationWorld.score());
        } while (scoreList.lastScoreWasImprovement());
        return scoreList;
    }


    public List<Pair<WorldScoreWithTrainingHistory, G>> runGeneticAlgorithmUntilStable() {
        List<Pair<WorldScoreWithTrainingHistory, G>> ret = Engine.<G, WorldScoreWithTrainingHistory>builder()
                .initialPopulation(100)
                .generationUsableSize(20)
                .mutationChance(.2)
                .evalFunction(g -> this.evaluate(g))
                .gene(brainFactory.geneSpec())
                .bestScoreReceiver(this::newBestScore)
                .otherScoresReceiver(this::otherScore)
                .build()
                .runGeneticAlgorithmUntilStable();
        visualizeScore(bestScore.getKey());
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }


    private void otherScore(WorldScoreWithTrainingHistory otherScore) {
        otherScore.cleanupMemory();

    }


    private void newBestScore(Pair<WorldScoreWithTrainingHistory, G> newTopScore) {
        this.bestScore = newTopScore;
        System.out.println("\nNew best score " + newTopScore.getKey() + " - " + newTopScore.getValue().toString());

    }

    private void visualizeScore(WorldScoreWithTrainingHistory score) {
        new JFXPanel();
        jfxVisualize = new JfxVisualize();
        Time time = score.getTime();
        time.reset();
        World simulationWorld = new World(time, score.getBrain());
        time.scheduleRecurringEvent(t -> jfxVisualize.addDataPoint(t.getTimeMilliSeconds(), simulationWorld.getOneleg().getPosition()), 100);
        time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);


        Platform.runLater(() -> {
            try {
                jfxVisualize.start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

}
