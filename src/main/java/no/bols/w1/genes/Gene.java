package no.bols.w1.genes;//
//

public abstract class Gene {

    public static double MUTATION_CHANCE = 0.2;

    public abstract Gene breed(Gene other);


}


