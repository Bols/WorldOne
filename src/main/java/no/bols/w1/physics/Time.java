package no.bols.w1.physics;//
//

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Time {
    @Getter
    private long timeMilliSeconds = 0;
    private TreeSet<Event> scheduledEvents = new TreeSet<Event>();
    int eventsHandled = 0;

    public void scheduleEvent(Consumer<Time> eventHandler, long timeOffsetMicroSeconds) {
        scheduledEvents.add(new Event(eventHandler, getTimeMilliSeconds() + timeOffsetMicroSeconds));
    }

    public void scheduleRecurringEvent(Consumer<Time> eventHandler, long milliseconds) {
        scheduledEvents.add(new RecurringEvent(eventHandler, getTimeMilliSeconds() + milliseconds, milliseconds));
    }

    public void runUntil(Predicate<Time> stopCriteria) {
        long startClockTime = System.currentTimeMillis();
        if (scheduledEvents.isEmpty()) {
            throw new RuntimeException("No scheduled events. Simulation not initialized");
        }
        while (!stopCriteria.test(this)) {
            for (int i = 0; i <= 100; i++) {  //replace with thread
                Event firstEvent = scheduledEvents.first();
                scheduledEvents.remove(firstEvent);
                timeMilliSeconds = firstEvent.timeOffsetMicroseconds;
                firstEvent.eventHandler.accept(this);
                firstEvent.afterEvent(this);
                eventsHandled++;
            }
        }

        System.out.println("Finished after " + eventsHandled + " events, simulated time passed " + String.format("%.2f", timeMilliSeconds / 1000f) + " real time passed " + (System.currentTimeMillis() - startClockTime) + "ms.");
    }


    @AllArgsConstructor
    private static class Event implements Comparable<Event> {
        protected Consumer<Time> eventHandler;
        protected long timeOffsetMicroseconds;

        public int compareTo(Event o) {
            return Long.compare(this.timeOffsetMicroseconds, o.timeOffsetMicroseconds);
        }

        public void afterEvent(Time time) {
        }
    }

    private static class RecurringEvent extends Event {
        private final long intervalMicroSeconds;

        public RecurringEvent(Consumer<Time> eventHandler, long time, long intervalMicroSeconds) {
            super(eventHandler, time);
            this.intervalMicroSeconds = intervalMicroSeconds;
        }

        @Override
        public void afterEvent(Time time) {
            time.scheduleRecurringEvent(eventHandler, intervalMicroSeconds);
        }
    }
}
