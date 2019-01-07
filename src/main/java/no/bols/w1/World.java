package no.bols.w1;//


import lombok.Getter;

public class World {
    final Oneleg oneleg;

    @Getter
    private final Time time;

    @Getter
    private final Food currentFood;


    public World(Time t, Brain brain) {
        this.oneleg = new Oneleg(this, brain, 0);
        this.time = t;
        this.currentFood = new Food(10, this);
    }


}
