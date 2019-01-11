package no.bols.w1.genes;//
//

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneMap {
    public Map<String, Gene> genes;

    public GeneMap() {
        genes = new HashMap<>();
    }

    public GeneMap(Map<String, Gene> genes) {
        this.genes = genes;
    }

    public GeneMap breed(GeneMap other) {
        Map<String, Gene> result = new HashMap<>();
        genes.forEach((name, gene) -> result.put(name, gene.breed(other.genes.get(name))));
        return new GeneMap(result);
    }

    @Override
    public String toString() {
        return genes.entrySet().stream().
                map(e -> e.getKey() + ":" + e.toString())
                .collect(Collectors.joining(","));
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
