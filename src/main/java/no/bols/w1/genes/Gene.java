package no.bols.w1.genes;//
//

public abstract class Gene {

    public static double MUTATION_CHANCE = 0.4;

    public abstract Gene breed(Gene other);


}


