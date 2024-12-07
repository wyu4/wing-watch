package com.WingWatch;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    private final String name;
    private final Function<ZonedDateTime, Integer> secondsLeftTillNext;

    public static final String N_GEYSER = "Polluted Geyser";
    public static final String N_GRANDMA = "Grandma's Dinner Event";
    public static final String N_TURTLE = "Sunset Sanctuary Turtle";
    public static final String N_SKATER = "Dream Skater";

    public EventData() {
        name = "Unknown";
        secondsLeftTillNext = (time) -> 0;
    }

    public EventData(String name, int cooldownMinutes, int minutesOffset) {
        this.name = name;
        this.secondsLeftTillNext = (time) -> {
            int currentTime = time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60);
            return (cooldownMinutes*60) - (currentTime - (minutesOffset*60) + (cooldownMinutes*60)) % (cooldownMinutes*60);
        };
    }

    public String getName() {
        return name;
    }

    public int getTimeLeft(ZonedDateTime skyTime) {
        return secondsLeftTillNext.apply(skyTime);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventData e) {
            return (e.getName().equals(this.getName())) && (e.secondsLeftTillNext.equals(this.secondsLeftTillNext));
        }
        return false;
    }
}
