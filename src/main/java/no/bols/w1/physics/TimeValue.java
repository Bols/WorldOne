package no.bols.w1.physics;//
//

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static no.bols.w1.physics.Time.zero;

@RequiredArgsConstructor
@Getter
public class TimeValue {
    public static TimeValue none = new TimeValue(zero, 0);
    private final Time.Instant valueInstant;
    private final double value;


    public double linearDecay(Time.Instant now, double maxTime) {

        long timeDiff = now.timeSince(valueInstant);
        if (timeDiff > maxTime)
            return 0;
        return timeDiff / maxTime * value;

    }

    public double timeSliceValueSince(TimeValue previousValue) {
        return valueInstant.timeSince(previousValue.getValueInstant()) * (value + previousValue.value) / 2;
    }
}
