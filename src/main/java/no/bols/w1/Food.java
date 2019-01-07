package no.bols.w1;//
//

import lombok.Getter;

public class Food extends PhysObject {
    @Getter
    private float foodAmount = 1.0f;

    public Food(float position, World world) {
        super(position, world);
    }

    public boolean eaten() {
        return foodAmount <= 0;
    }

    public void eat(float amount) {
        if (foodAmount < 0) {
            throw new RuntimeException("no food");
        }
        foodAmount = foodAmount - amount;
    }
}
