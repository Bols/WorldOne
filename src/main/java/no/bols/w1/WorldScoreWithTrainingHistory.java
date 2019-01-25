package no.bols.w1;//
//

import lombok.Getter;
import no.bols.w1.physics.WorldScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WorldScoreWithTrainingHistory implements Comparable<WorldScoreWithTrainingHistory> {
    private WorldScore bestScore;
    private List<WorldScore> history = new ArrayList<>();

    public void addScore(WorldScore newScore) {
        history.add(newScore);
        if (bestScore == null || newScore.compareTo(bestScore) > 0) {
            bestScore = newScore;
        }
    }

    public boolean lastScoreWasImprovement() {
        return history.size() < 2 || history.get(history.size() - 1).compareTo(history.get(history.size() - 2)) > 0;
    }

    @Override
    public int compareTo(WorldScoreWithTrainingHistory o) {
        return bestScore.compareTo(o.getBestScore());
    }

    @Override
    public String toString() {
        String score = f(bestScore.getScore()) + " dist:" + f(bestScore.getDistanceTraveled()) + " food:" + bestScore.getFoodEaten();
        String historyString = history.size() < 3 ? "" : "[" + history.stream().map(h -> f(h.getScore())).collect(Collectors.joining(",")) + "]";
        return score + historyString;
    }

    public String f(double d) {
        return String.format("%3.2f", d);
    }
}
