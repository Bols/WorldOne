package no.bols.w1;//
//

import lombok.Setter;


public class Brain {
    @Setter
    protected Oneleg oneleg;

    private Time time;

    public Brain(Time time) {
        this.time = time;
    }


}
