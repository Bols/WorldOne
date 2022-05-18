package no.bols.w1.genes;//
//

public abstract class GeneScore implements Comparable<GeneScore> {

    public abstract double getScore();

    @Override
    public int compareTo(GeneScore o) {
        return Double.compare(this.getScore(), o.getScore());
    }
}
