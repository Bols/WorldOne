package no.bols.w1.physics;//
//

import lombok.Getter;

public class PhysObject {
    @Getter
    protected double position;
    protected World world;

    public PhysObject(double position, World world) {
        this.position = position;
        this.world = world;
    }

    public double distance(PhysObject obj) {
        return Math.abs(obj.getPosition() - getPosition());
    }


}
