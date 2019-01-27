package no.bols.w1.genes.internal;//
//

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneMap {
    public Map<String, GeneValue> genes;

    public GeneMap() {
        genes = new HashMap<>();
    }

    public GeneMap(Map<String, GeneValue> genes) {
        this.genes = genes;
    }

    public GeneMap breed(GeneMap other, double mutationChance) {
        Map<String, GeneValue> result = new HashMap<>();
        genes.forEach((name, gene) -> result.put(name, gene.breed(other.genes.get(name), mutationChance)));
        return new GeneMap(result);
    }

    @Override
    public String toString() {
        return "{" + genes.entrySet().stream().
                map(e -> e.toString())
                .collect(Collectors.joining(",")) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneMap geneMap = (GeneMap) o;
        return genes.equals(geneMap.genes);
    }

    @Override
    public int hashCode() {
        return genes.hashCode();
    }
}
