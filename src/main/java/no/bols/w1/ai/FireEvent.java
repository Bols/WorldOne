package no.bols.w1.ai;//
//

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FireEvent {
    private long time;
    private Neuron source;
}
