package no.bols.w1.physics;//
//

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WorldScore implements Comparable<WorldScore> {
    private double score;
    private double distanceTraveled;
    private double foodEaten;

    @Override
    public int compareTo(WorldScore o) {
        return Double.compare(score, o != null ? o.score : 0);
    }
}
