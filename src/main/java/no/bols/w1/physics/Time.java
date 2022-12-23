package no.bols.w1.physics;//
//

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Time {
    public static long eventid=0;
    private long timeMilliSeconds;
    private long iteration = 0;
    private PriorityQueue<Event> scheduledEvents;
    @Getter
    int eventsHandled;
    private List<RecurringEvent> recurringEventsList = new ArrayList<>();
    @Getter
    private long realClockRuntimeNs;

    private boolean gatherStats = false;

    @Getter
    private Map<String, DoubleAdder> stats = new HashMap<>();

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
        long startClockTime = System.nanoTime();
        if (scheduledEvents.isEmpty()) {
            throw new RuntimeException("No scheduled events. Simulation not initialized");
        }
        while (!stopCriteria.test(this) && !timeOut) {
                Event firstEvent = scheduledEvents.remove();
                timeMilliSeconds = firstEvent.timeMs;
                firstEvent.eventHandler.accept(this);     //TODO: eventhandler bør også inneholde en metode for å oppdatere tilstand til siste
                firstEvent.afterEvent(this);
                eventsHandled++;
//            long realtime = System.currentTimeMillis() - startClockTime;
//            if (scheduledEvents.size() > 10000 || (realtime > 10000 && timeMilliSeconds < realtime)) {
//                System.err.println("Scenario-run timeout. Realtime=" + realtime + ", simulated time=" + timeMilliSeconds + ", queuesize=" + scheduledEvents.size());
//                timeOut = true;
//            }
        }
        realClockRuntimeNs = System.nanoTime() - startClockTime;

        //System.out.print("Finished after " + eventsHandled + " events, simulated time passed " + String.format("%.2f", timeMilliSeconds / 1000f) + " real time passed " + (System.currentTimeMillis() - startClockTime) + "ms.");
    }


    public void runSingleEvent(Consumer<Time> eventHandler, long timeOffsetMilliseconds) {
        this.scheduleEvent(eventHandler, timeOffsetMilliseconds);
        this.runUntil(allEventsDone());
    }

    public void reset() {
        iteration++;
        timeMilliSeconds = 0;
        scheduledEvents = new PriorityQueue<>();
        eventsHandled = 0;
        recurringEventsList.forEach(e -> {e.timeMs=e.intervalMilliseconds;scheduledEvents.add(e);});
        stats = new HashMap<>();
    }

    public void unScheduleRecurringEvent(RecurringEvent event) {
        recurringEventsList.remove(event);
    }


    public Instant getSimulatedTime() {
        return new Instant(iteration, timeMilliSeconds);
    }

    public long timeSince(Instant startTime) {
        return getSimulatedTime().timeSince(startTime);
    }

    private static class Event implements Comparable<Event> {
        private final long id;
        protected Consumer<Time> eventHandler;
        protected long timeMs;

        public Event(Consumer<Time> eventHandler, long timeMs) {
            this.eventHandler = eventHandler;
            this.timeMs = timeMs;
            this.id = eventid++;
        }

        public int compareTo(Event o) {
            int timeCompare = Long.compare(this.timeMs, o.timeMs);
            if(timeCompare==0){
                return Long.compare(this.id,o.id);
            }
            return timeCompare;
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
            this.timeMs=time.timeMilliSeconds+intervalMilliseconds;
            time.scheduledEvents.add(this);
        }
    }

    public void incrementStat(String type) {
        if (gatherStats) {
            DoubleAdder stat = stats.computeIfAbsent(type, k -> new DoubleAdder());
            stat.add(1);
        }
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
                return 10000; //
            }
            if (comparedTime.iteration == iteration) {
                return timeMs - comparedTime.timeMs;
            }
            // If in different iterations, use start time instead, as comparedTime is meaningless
            return timeMs;
        }
    }

    public void gatherStats(boolean gather) {
        this.gatherStats = gather;
    }

    public final static Instant zero = new Instant(0, 0);

    public Predicate<Time> allEventsDone() {
        return (time -> this.scheduledEvents.isEmpty());
    }
}
