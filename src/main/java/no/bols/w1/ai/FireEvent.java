package no.bols.w1.ai;//
//

import lombok.AllArgsConstructor;
import lombok.Data;
import no.bols.w1.physics.Time;

@Data
@AllArgsConstructor
public class FireEvent {
    private Time.Instant time;
    private Neuron source;
}
