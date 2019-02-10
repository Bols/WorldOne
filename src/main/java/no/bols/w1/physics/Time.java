package no.bols.w1.physics;//
//

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Time {
    private long timeMilliSeconds;
    private long iteration = 0;
    private PriorityQueue<Event> scheduledEvents;
    @Getter
    int eventsHandled;
    private List<RecurringEvent> recurringEventsList = new ArrayList<>();
    @Getter
    private long realClockRuntime;

    private int neuronFireCountStat = 0;

    public Time() {
        reset();
    }

    public void scheduleEvent(Consumer<Time> eventHandler, long timeOffsetMilliseconds) {
        scheduledEvents.add(new Event(eventHandler, timeMilliSeconds + timeOffsetMilliseconds));
    }

    public RecurringEvent scheduleRecurringEvent(Consumer<Time> eventHandler, long milliseconds) {
        RecurringEvent event = new RecurringEvent(eventHandler, timeMilliSeconds + milliseconds, milliseconds);
        recurringEventsList.add(event);
        scheduledEvents.add(event);
        return event;
    }

    public void runUntil(Predicate<Time> stopCriteria) {
        boolean timeOut = false;
        long startClockTime = System.currentTimeMillis();
        if (scheduledEvents.isEmpty()) {
            throw new RuntimeException("No scheduled events. Simulation not initialized");
        }
        while (!stopCriteria.test(this) && !timeOut) {
            for (int i = 0; i <= 1000; i++) {  //replace with thread
                Event firstEvent = scheduledEvents.remove();
                timeMilliSeconds = firstEvent.timeOffsetMilliSeconds;
                firstEvent.eventHandler.accept(this);     //TODO: eventhandler bør også inneholde en metode for å oppdatere tilstand til siste
                firstEvent.afterEvent(this);
                eventsHandled++;
            }
            long realtime = System.currentTimeMillis() - startClockTime;
//            if (scheduledEvents.size() > 10000 || (realtime > 10000 && timeMilliSeconds < realtime)) {
//                System.err.println("Scenario-run timeout. Realtime=" + realtime + ", simulated time=" + timeMilliSeconds + ", queuesize=" + scheduledEvents.size());
//                timeOut = true;
//            }
        }
        realClockRuntime = System.currentTimeMillis() - startClockTime;

        //System.out.print("Finished after " + eventsHandled + " events, simulated time passed " + String.format("%.2f", timeMilliSeconds / 1000f) + " real time passed " + (System.currentTimeMillis() - startClockTime) + "ms.");
    }

    public void reset() {
        iteration++;
        timeMilliSeconds = 0;
        scheduledEvents = new PriorityQueue<>();
        eventsHandled = 0;
        recurringEventsList.forEach(scheduledEvents::add);
        neuronFireCountStat = 0;

    }

    public void unScheduleRecurringEvent(RecurringEvent event) {
        recurringEventsList.remove(event);
    }

    public int getNeuronFireCountStat() {
        return neuronFireCountStat;
    }

    public Instant getSimulatedTime() {
        return new Instant(iteration, timeMilliSeconds);
    }

    public long timeSince(Instant startTime) {
        return getSimulatedTime().timeSince(startTime);
    }


    @AllArgsConstructor
    private static class Event implements Comparable<Event> {
        protected Consumer<Time> eventHandler;
        protected long timeOffsetMilliSeconds;

        public int compareTo(Event o) {
            return Long.compare(this.timeOffsetMilliSeconds, o.timeOffsetMilliSeconds);
        }

        public void afterEvent(Time time) {
        }
    }

    public static class RecurringEvent extends Event {
        private final long intervalMilliseconds;

        public RecurringEvent(Consumer<Time> eventHandler, long time, long intervalMilliseconds) {
            super(eventHandler, time);
            this.intervalMilliseconds = intervalMilliseconds;
        }

        @Override
        public void afterEvent(Time time) {
            time.scheduledEvents.add(new RecurringEvent(eventHandler, time.timeMilliSeconds + intervalMilliseconds, intervalMilliseconds));
        }
    }

    public void addNeuronFireCountStat() {
        this.neuronFireCountStat++;
    }


    public static class Instant {
        private long timeMs;
        private long iteration;

        Instant(long iteration, long timeMs) {
            this.timeMs = timeMs;
            this.iteration = iteration;
        }

        public long ms() {
            return timeMs;
        }

        public long timeSince(Instant comparedTime) {
            if (comparedTime == null) {
                return timeMs;
            }
            if (comparedTime.iteration == iteration) {
                return timeMs - comparedTime.timeMs;
            }
            // If in different iterations, use start time instead, as comparedTime is meaningless
            return timeMs;
        }
    }
}
