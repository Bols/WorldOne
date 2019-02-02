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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class World1SimulatorRunner<G> {
    private int scenarioTimeMs;
    private long realStartTime = System.currentTimeMillis();
    BrainFactory<G> brainFactory;
    private JfxVisualize jfxVisualize;
    private Pair<WorldScoreWithTrainingHistory, G> bestScore;

    List<Pair<Long, Double>> bestScoreHistory = new ArrayList<>();

    @Builder
    public World1SimulatorRunner(int scenarioTimeMs, BrainFactory<G> brainFactory) {
        this.scenarioTimeMs = scenarioTimeMs;
        this.brainFactory = brainFactory;
    }

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
                .generationUsableSize(50)
                .evalFunction(g -> this.evaluate(g))
                .gene(brainFactory.geneSpec())
                .bestScoreReceiver(this::newBestScore)
                .otherScoresReceiver(this::otherScore)
                .build()
                .runGeneticAlgorithmUntilStable();
        visualizeScore(bestScore);
        return bestScore;
    }


    private void otherScore(WorldScoreWithTrainingHistory otherScore) {
        otherScore.cleanupMemory();

    }


    private void newBestScore(Pair<WorldScoreWithTrainingHistory, G> newTopScore) {
        bestScoreHistory.add(new Pair((System.currentTimeMillis() - realStartTime), newTopScore.getKey().getBestScore().getScore()));
        this.bestScore = newTopScore;

        // System.out.println("\nNew best score " + newTopScore.getKey() + " - " + newTopScore.getValue().toString());
    }

    public void visualizeScore(Pair<WorldScoreWithTrainingHistory, G> score) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new JFXPanel();
        jfxVisualize = new JfxVisualize();
        Time time = score.getKey().getTime();
        time.reset();
        Brain blankBrain = brainFactory.createBrain(time, score.getValue());
        WorldScoreWithTrainingHistory scoreList = new WorldScoreWithTrainingHistory(time, blankBrain);
        Time.RecurringEvent graphEvent = null;
        do {

            if (graphEvent != null) {
                time.unScheduleRecurringEvent(graphEvent);
            }
            time.reset();
            World simulationWorld = new World(time, blankBrain);
            AtomicInteger neuronFires = new AtomicInteger(0);
            graphEvent = time.scheduleRecurringEvent(t -> {
                jfxVisualize.addDataPoint("Position", scoreList.getHistory().size(), t.getTimeMilliSeconds(), simulationWorld.getOneleg().getPosition());
                jfxVisualize.addDataPoint("Fire", scoreList.getHistory().size(), t.getTimeMilliSeconds(), (simulationWorld.getTime().getNeuronFireCountStat() - neuronFires.get()));
                neuronFires.set(simulationWorld.getTime().getNeuronFireCountStat());
            }, 100);

            time.runUntil(t -> simulationWorld.getTime().getTimeMilliSeconds() > scenarioTimeMs);
            scoreList.addScore(simulationWorld.score());
        } while (scoreList.lastScoreWasImprovement());

        for (Pair<Long, Double> longDoublePair : bestScoreHistory) {
            jfxVisualize.addDataPoint("Gene tuning best score", 0, longDoublePair.getKey(), longDoublePair.getValue());
        }
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
        Platform.exit();
        System.out.println("----------- Result stats ----------------");
        System.out.println(score.getKey());
        System.out.println("Real-clock runtime:  " + time.getRealClockRuntime());
        System.out.println("Events handled: " + time.getEventsHandled());
        System.out.println("Neurons fired: " + time.getNeuronFireCountStat());
    }

}
