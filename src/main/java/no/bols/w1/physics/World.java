package no.bols.w1.physics;//


import lombok.Getter;

public class World {
    @Getter
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

    public WorldScore score() {
        double eaten = foodEaten + (1 - currentFood.getFoodAmount());
        double moveAmount = oneleg.getPosition() / 100.0;
        double score = eaten + (moveAmount > 1.0 ? 1.0 : moveAmount);
        return new WorldScore(score, oneleg.getPosition(), foodEaten);
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
