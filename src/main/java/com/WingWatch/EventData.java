package com.WingWatch;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    private final String name;
    private final Function<ZonedDateTime, Integer> timeLeft;
    private final int cooldown, duration;

    public static final String N_GEYSER = "Polluted Geyser";
    public static final String N_GRANDMA = "Grandma's Dinner Event";
    public static final String N_TURTLE = "Sunset Sanctuary Turtle";
    public static final String N_SKATER = "Dream Skater";

    public EventData() {
        name = "Unknown";
        timeLeft = (time) -> 0;
        cooldown = 0;
        duration = 0;
    }

    public EventData(String name, int cooldownSeconds, int offsetSeconds, int durationSeconds) {
        this.name = name;
        this.timeLeft =
                (time) -> (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60)) - (offsetSeconds) + (cooldownSeconds))
                        % (cooldownSeconds);
        this.cooldown = cooldownSeconds;
        this.duration = durationSeconds;
    }

    public String getName() {
        return name;
    }

    public int getTimeLeft(ZonedDateTime skyTime) {
        return timeLeft.apply(skyTime);
    }

    public boolean active(ZonedDateTime skyTime) {
        return (cooldown - getTimeLeft(skyTime)) <= duration;
    }

    public float percentElapsed(ZonedDateTime skyTime) {
        if (active(skyTime)) {
            return Math.clamp(1f - ((float) (cooldown - getTimeLeft(skyTime)) / duration), 0, 1f);
        } else {
            return Math.clamp(((float) ((cooldown - duration) - getTimeLeft(skyTime)) / (cooldown - duration)), 0, 1f);
        }
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
