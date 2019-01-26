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
import java.util.concurrent.CountDownLatch;


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


    public Pair<WorldScoreWithTrainingHistory, G> runGeneticAlgorithmUntilStable() {
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
        return bestScore;
    }


    private void otherScore(WorldScoreWithTrainingHistory otherScore) {
        otherScore.cleanupMemory();

    }


    private void newBestScore(Pair<WorldScoreWithTrainingHistory, G> newTopScore) {
        this.bestScore = newTopScore;
        System.out.println("\nNew best score " + newTopScore.getKey() + " - " + newTopScore.getValue().toString());

    }

    public void visualizeScore(WorldScoreWithTrainingHistory score) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new JFXPanel();
        jfxVisualize = new JfxVisualize();
        Time time = score.getTime();
        time.reset();
        World simulationWorld = new World(time, score.getBrain());
        time.scheduleRecurringEvent(t -> jfxVisualize.addDataPoint("oneleg", t.getTimeMilliSeconds(), simulationWorld.getOneleg().getPosition()), 100);
        time.scheduleRecurringEvent(t -> jfxVisualize.addDataPoint("output", t.getTimeMilliSeconds(), simulationWorld.getOneleg().getLastMotorOutput()), 100);
        time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);


        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setOnHiding(event -> countDownLatch.countDown());
            jfxVisualize.start(stage);
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
