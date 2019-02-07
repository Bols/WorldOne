package no.bols.w1;//
//

import lombok.Getter;
import no.bols.w1.physics.Brain;
import no.bols.w1.physics.Time;
import no.bols.w1.physics.WorldScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorldScoreWithTrainingHistory implements Comparable<WorldScoreWithTrainingHistory> {
    private Time time;
    private Brain brain;
    private List<WorldScore> history = new ArrayList<>();

    public WorldScoreWithTrainingHistory(Time time, Brain brain) {
        this.time = time;
        this.brain = brain;
    }

    public static WorldScoreWithTrainingHistory nullScore() {
        return new WorldScoreWithTrainingHistory(null, null).addScore(new WorldScore(0, 3, 0));
    }

    public WorldScoreWithTrainingHistory addScore(WorldScore newScore) {
        history.add(newScore);
        return this;
    }

    public WorldScore score() {
        return history.get(history.size() - 1);
    }

    public boolean lastScoreWasImprovement() {
        return history.size() < 2 || history.get(history.size() - 1).compareTo(history.get(history.size() - 2)) > 0;
    }

    @Override
    public int compareTo(WorldScoreWithTrainingHistory o) {
        return score().compareTo(o.score());
    }

    @Override
    public String toString() {
        String score = f(score().getScoreValue()) + " dist:" + f(score().getDistanceTraveled()) + " food:" + score().getFoodEaten();
        String historyString = history.size() < 2 ? "" : "[" + history.stream().map(h -> f(h.getScoreValue())).collect(Collectors.joining(",")) + "]";
        return score + historyString;
    }

    public String f(double d) {
        return String.format("%3.2f", d);
    }

    public void cleanupMemory() {
        this.time = null;
        this.brain = null;
    }
}
