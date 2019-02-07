package no.bols.w1.physics;//
//

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WorldScore implements Comparable<WorldScore> {
    private double scoreValue;
    private double distanceTraveled;
    private double foodEaten;

    @Override
    public int compareTo(WorldScore o) {
        return Double.compare(scoreValue, o != null ? o.scoreValue : 0);
    }
}
