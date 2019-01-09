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
}
