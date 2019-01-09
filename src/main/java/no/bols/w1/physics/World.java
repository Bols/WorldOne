package no.bols.w1.physics;//


import lombok.Getter;

public class World {
    final Oneleg oneleg;

    int foodEaten = 0;
    double lastFoodPosition = 0;

    @Getter
    private final Time time;

    @Getter
    private Food currentFood;


    public World(Time t, Brain brain) {
        this.oneleg = new Oneleg(this, brain, 0);
        this.time = t;
        placeFood();
    }

    public double score() {
        double eaten = foodEaten + (1 - currentFood.getFoodAmount());
        double moveAmount = oneleg.getPosition() / 1000;
        return eaten + (moveAmount > 1.0 ? 1.0 : moveAmount);
    }


    public void foodEaten(Food food) {
        foodEaten++;
        placeFood();
    }

    private void placeFood() {
        double newPosition = lastFoodPosition + 10;
        this.currentFood = new Food(newPosition, this);
        lastFoodPosition = newPosition;
    }
}
