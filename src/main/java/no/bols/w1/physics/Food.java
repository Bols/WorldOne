package no.bols.w1.physics;//
//

import lombok.Getter;

public class Food extends PhysObject {
    @Getter
    private double foodAmount = 1.0;

    public Food(double position, World world) {
        super(position, world);
    }

    public boolean eaten() {
        return foodAmount <= 0;
    }

    public void eat(double amount) {
        if (foodAmount < 0) {
            throw new RuntimeException("empty food");
        }
        foodAmount = foodAmount - amount;
        if (foodAmount < 0) {
            world.foodEaten(this);
        }
    }
}
