package com.WingWatch;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    public enum TimeType {
        SKY, LOCAL
    }

    private final String name;
    private final Function<ZonedDateTime, Long> timeLeft;
    private final long cooldown, duration;
    private final TimeType timeType;


    public EventData() {
        name = "Unknown";
        timeLeft = (time) -> 0L;
        cooldown = 0;
        duration = 0;
        timeType = TimeType.SKY;
    }

    public EventData(String name, long cooldownSeconds, long offsetSeconds, long durationSeconds, TimeType timeType) {
        this.name = name;
        this.timeType = timeType;
        this.timeLeft =
                (time) -> {
                    if (timeType == TimeType.LOCAL) {
                        time = ZonedDateTime.now();
                    }
                    return (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60)) - (offsetSeconds) + (cooldownSeconds))
                            % (cooldownSeconds);
                };
        this.cooldown = cooldownSeconds;
        this.duration = durationSeconds;
    }

    public EventData(String name, DayOfWeek dayOfWeek, long offsetSeconds, long duration, TimeType timeType) {
        this.name = name;
        this.timeType = timeType;
        this.timeLeft =
                (time) -> {
                    if (timeType == TimeType.LOCAL) {
                        time = ZonedDateTime.now();
                    }
                    long timeElapsed = (time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + ((long) time.getDayOfWeek().getValue() *24*60*60));
                    long event = (((long) dayOfWeek.getValue()*24*60*60) + offsetSeconds);
                    long modResult = (event-timeElapsed)%(7*24*60*60);
                    if (modResult < 0) modResult += (7*24*60*60);
                    return modResult;
                };
        this.cooldown = 7*24*60*60;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public long getTimeLeft(ZonedDateTime skyTime) {
        return timeLeft.apply(skyTime);
    }

    public boolean active(ZonedDateTime skyTime) {
        return (cooldown - getTimeLeft(skyTime)) <= duration;
    }

    public TimeType getTimeType() {
        return timeType;
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
