package com.WingWatch;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class EventData {
    public enum TimeType {
        SKY, LOCAL
    }

    private final String name;
    private final Function<ZonedDateTime, Long> timeLeft;
    private long cooldown, duration;
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
                    return (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + (time.getDayOfYear()*24*60*60) + ((long) time.getYear() *365*24*60*60)) - (offsetSeconds) + (cooldownSeconds))
                            % (cooldownSeconds);
                };
        this.cooldown = cooldownSeconds;
        this.duration = Math.max(durationSeconds, 10);
    }

    public EventData(String name, Integer[] days, int cooldownSeconds, int offsetSeconds, int durationSeconds, TimeType timeType) {
        ArrayList<Integer> onDays = new ArrayList<>(List.of(days));
        if (onDays.isEmpty()) {
            onDays.addAll(List.of(1,2,3,4,5,6,7));
        } else {
            onDays.removeIf(day -> day < 1 || day > 7);
            Collections.sort(onDays);
        }

        this.name = name;
        this.timeType = timeType;
        this.timeLeft =
                (time) -> {
                    if (timeType == TimeType.LOCAL) {
                        time = ZonedDateTime.now();
                    }

                    // Convert ZonedDateTime to day of week (1 = Monday, ..., 7 = Sunday)
                    int currentDay = time.getDayOfWeek().getValue();
                    long secondsSinceMidnight = time.getSecond() + (time.getHour() * 60*60);
                    long secondsUntilMidnight = 24 * 60 * 60 - secondsSinceMidnight;

                    // Check for events on the current day
                    if (onDays.contains(currentDay)) {
                        if (onDays.getFirst() == currentDay) {
                            this.cooldown = (long) (7-onDays.getLast()+onDays.getFirst()) * 24*60*60;
                        } else {
                            this.cooldown = cooldownSeconds;
                        }

                        long elapsedSinceOffset = Math.max(0, secondsSinceMidnight - offsetSeconds);

                        long nextEventToday = secondsSinceMidnight + cooldownSeconds - (elapsedSinceOffset % cooldownSeconds);

                        if (nextEventToday < 24 * 60 * 60) { // If event still falls on the same day
                            return cooldownSeconds - (elapsedSinceOffset % cooldownSeconds);
                        }
                    }

                    // Find the next available day
                    this.cooldown = (long) (7-onDays.getLast()+onDays.getFirst()) * 24*60*60;
                    for (int day : onDays) {
                        if (day > currentDay) {
                            return secondsUntilMidnight + (long) ((day - currentDay) - 1) * 24 * 60 * 60 + offsetSeconds;
                        }
                    }

                    // If no more events this week, wrap to the first day of the next week
                    return secondsUntilMidnight + (long) ((7 - currentDay + onDays.getFirst()) - 1) * 24 * 60 * 60 + offsetSeconds;

//                    int today = time.getDayOfWeek().getValue();
//                    Integer nextEventDay = getNextEventDay(onDays, today);
//
//                    int daysRemaining;
//                    if (today <= nextEventDay) {
//                        daysRemaining = nextEventDay - today;
//                    } else {
//                        daysRemaining = 7 - (today - nextEventDay);
//                    }
//
//                    long daysRemainingInSeconds = ((long) daysRemaining *24*60*60);
//                    this.cooldown = ((long) daysRemaining *24*60*60) + cooldownSeconds;
//
//                    return getNextDailyOccurrence(time, this.cooldown, offsetSeconds);
                };
        this.cooldown = cooldownSeconds;
        this.duration = durationSeconds;
    }

//    private static Integer getNextEventDay(ArrayList<Integer> onDays, int today) {
//        Integer nextEventDay = onDays.getLast();
//        if (today > nextEventDay) {
//            nextEventDay = onDays.getFirst();
//        } else {
//            for (Integer day : onDays) {
//                if (today > day) {
//                    continue;
//                }
//                if (day - today <= nextEventDay - today) {
//                    nextEventDay = day;
//                }
//            }
//        }
//        return nextEventDay;
//    }

    public String getName() {
        return name;
    }

    public long getTimeLeft(ZonedDateTime skyTime) {
//        if (name.equals("Weekly Reset")) {
//            System.out.println(SkyClock.formatTimeLeft(cooldown) + "   -   " + SkyClock.formatTimeLeft(timeLeft.apply(skyTime)));
//        }
        return timeLeft.apply(skyTime);
    }

    public boolean active(ZonedDateTime skyTime) {
//        return false;
        return (cooldown - getTimeLeft(skyTime)) <= duration;
    }

    public float percentElapsed(ZonedDateTime skyTime) {
        if (active(skyTime)) {
            return Math.clamp(1f - ((float) (cooldown - getTimeLeft(skyTime)) / duration), 0, 1f);
        } else {
            return Math.clamp(((float) ((cooldown - duration) - getTimeLeft(skyTime)) / (cooldown - duration)), 0, 1f);
        }
    }

    public long durationLeft(ZonedDateTime skyTime) {
        if (active(skyTime)) {
            return Math.clamp(duration-(cooldown - getTimeLeft(skyTime)), 0, duration);
        }
        return 0;
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
