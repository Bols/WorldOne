package no.bols.w1.physics;//
//

import lombok.Setter;


public abstract class Brain {
    @Setter
    protected Oneleg oneleg;

    protected Time time;

    public Brain(Time time) {
        this.time = time;
    }

}
