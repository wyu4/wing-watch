package com.WingWatch;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    private final String name;
    private final Function<ZonedDateTime, Integer> timeLeft;

    public static final String N_GEYSER = "Polluted Geyser";
    public static final String N_GRANDMA = "Grandma's Dinner Event";
    public static final String N_TURTLE = "Sunset Sanctuary Turtle";
    public static final String N_SKATER = "Dream Skater";

    public EventData() {
        name = "Unknown";
        timeLeft = (time) -> 0;
    }

    public EventData(String name, int cooldownMinutes, int minutesOffset) {
        this.name = name;
        this.timeLeft =
                (time) -> (cooldownMinutes*60) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60)) - (minutesOffset*60) + (cooldownMinutes*60))
                        % (cooldownMinutes*60);
    }

    public String getName() {
        return name;
    }

    public int getTimeLeft(ZonedDateTime skyTime) {
        return timeLeft.apply(skyTime);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventData e) {
            return (e.getName().equals(this.getName())) && (e.timeLeft.equals(this.timeLeft));
        }
        return false;
    }
}
