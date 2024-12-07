package com.WingWatch;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    private String name = "unnamed";
    private Function<ZonedDateTime, Integer> secondsLeftTillNext = (s) -> 0;

    public static final String N_GEYSER = "Polluted Geyser";
    public static final String N_GRANDMA = "Grandma's Dinner Event";
    public static final String N_TURTLE = "Sunset Sanctuary Turtle";
    public static final String N_SKATER = "Dream Skater";

    public static final EventData[] WAX_EVENTS = {
            createWaxEvent(N_GEYSER, 2*60, 5),
            createWaxEvent(N_GRANDMA, 2*60, 35),
            createWaxEvent(N_TURTLE, 2*60, 50),
            createWaxEvent(N_SKATER, 2*60, 60)
    };

    private static EventData createWaxEvent(String name, int cooldownMinutes, int minutesOffset) {
        EventData waxEvent = new EventData();
        waxEvent.setName(name);
        waxEvent.setSecondsLeftTillNext(
                (time) -> {
                    int currentTime = time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60);
                    return (cooldownMinutes*60) - (currentTime - (minutesOffset*60) + (cooldownMinutes*60)) % (cooldownMinutes*60);
                }
        );
        return waxEvent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSecondsLeftTillNext(
            Function<ZonedDateTime, Integer> secondsLeftTillNext
    ) {
        this.secondsLeftTillNext = secondsLeftTillNext;
    }

    public int getSecondsLeftTillNext(ZonedDateTime skyTime) {
        return secondsLeftTillNext.apply(skyTime);
    }

    @Override
    public String toString() {
        int remainingTime = getSecondsLeftTillNext(SkyClock.getSkyTime());
        return "{" + name + ":" +
                String.format("%02d:%02d:%02d:%02d",
                        remainingTime/(60*60*24),
                        remainingTime/(60*60) % 24,
                        remainingTime/(60) % 60,
                        remainingTime % 60
                        ) +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventData e) {
            return (e.getName().equals(this.getName())) && (e.secondsLeftTillNext.equals(this.secondsLeftTillNext));
        }
        return false;
    }
}
