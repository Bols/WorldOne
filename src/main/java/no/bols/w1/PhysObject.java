package no.bols.w1;//
//

import lombok.Getter;

public class PhysObject {
    @Getter
    protected float position;
    protected World world;

    public PhysObject(float position, World world) {
        this.position = position;
        this.world = world;
    }

    public float distance(PhysObject obj) {
        return Math.abs(obj.getPosition() - getPosition());
    }


}
