package no.bols.w1.genes;//
//

public abstract class Gene {

    public static float MUTATION_CHANCE = 0.3f;

    public abstract Gene breed(Gene other);


}


