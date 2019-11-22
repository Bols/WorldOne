package no.bols.w1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Builder;
import no.bols.w1.genes.Engine;
import no.bols.w1.genes.GeneticAlgorithm;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.JfxVisualize;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;


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
            Time.Instant startTime = time.getSimulatedTime();
            World simulationWorld = new World(time, brain);
            time.runUntil(t -> time.timeSince(startTime) > scenarioTimeMs);
            scoreList.addScore(simulationWorld.score());
            time.reset();
        } while (scoreList.lastScoreWasImprovement());
        return scoreList;
    }


    public Pair<WorldScoreWithTrainingHistory, G> runGeneticAlgorithmUntilStable() {
        List<Pair<WorldScoreWithTrainingHistory, G>> ret = Engine.<G, WorldScoreWithTrainingHistory>builder()
                .initialPopulation(100)
                .geneticAlgorithm(GeneticAlgorithm.<WorldScoreWithTrainingHistory>builder().generationUsableSize(50).build())
                .evalFunction(g -> this.evaluate(g))
                .gene(brainFactory.geneSpec())
                .bestScoreReceiver(this::newBestScore)
                .filterInitialPopulation(p -> p.getKey().score().getScoreValue() > .01)
                // .parallellism(1)
                .build()
                .runGeneticAlgorithmUntilStable();
        return bestScore;
    }

    private void newBestScore(Pair<WorldScoreWithTrainingHistory, G> newTopScore) {
        bestScoreHistory.add(new Pair((System.currentTimeMillis() - realStartTime), newTopScore.getKey().score().getScoreValue()));
        this.bestScore = newTopScore;

        // System.out.println("\nNew best scoreValue " + newTopScore.getKey() + " - " + newTopScore.getValue().toString());
    }

    public WorldScoreWithTrainingHistory rerunAndVisualize(Pair<WorldScoreWithTrainingHistory, G> score) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new JFXPanel();
        jfxVisualize = new JfxVisualize();
        Time time = score.getKey().getTime();
        time.reset();
        time.gatherStats(true);
        Brain blankBrain = brainFactory.createBrain(time, score.getValue());
        WorldScoreWithTrainingHistory scoreList = new WorldScoreWithTrainingHistory(time, blankBrain);
        Time.RecurringEvent graphEvent = null;
        do {

            if (graphEvent != null) {
                time.unScheduleRecurringEvent(graphEvent);
            }
            time.reset();
            Time.Instant startTime = time.getSimulatedTime();
            World simulationWorld = new World(time, blankBrain);
            Map<String, Double> oldValues = new HashMap<>();
            graphEvent = time.scheduleRecurringEvent(t -> {
                jfxVisualize.addDataPoint("Position", scoreList.getHistory().size(), t.getSimulatedTime().ms(), simulationWorld.getOneleg().getPosition());
                jfxVisualize.addDataPoint("Motoroutput", scoreList.getHistory().size(), t.getSimulatedTime().ms(), simulationWorld.getOneleg().getLastMotorOutput());
                for (Map.Entry<String, DoubleAdder> statEntry : time.getStats().entrySet()) {
                    Double previousValue = oldValues.get(statEntry.getKey());
                    double newValue = statEntry.getValue().doubleValue();
                    jfxVisualize.addDataPoint(statEntry.getKey(), scoreList.getHistory().size(), t.getSimulatedTime().ms(), newValue - (previousValue != null ? previousValue : 0));
                    oldValues.put(statEntry.getKey(), newValue);
                }


            }, 100);

            time.runUntil(t -> t.timeSince(startTime) > scenarioTimeMs);
            scoreList.addScore(simulationWorld.score());
        } while (scoreList.lastScoreWasImprovement());

        for (Pair<Long, Double> longDoublePair : bestScoreHistory) {
            jfxVisualize.addDataPoint("Gene tuning best scoreValue", 0, longDoublePair.getKey(), longDoublePair.getValue());
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
        System.out.println("Stats: " + time.getStats().entrySet().stream().map(e -> e.getKey() + ":" + e.getValue().doubleValue()).collect(Collectors.joining(",")));
        return scoreList;
    }

}
