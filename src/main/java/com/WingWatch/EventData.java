package com.WingWatch;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class EventData {
    public enum TimeType {
        SKY, LOCAL
    }

    private static EventData[] WAX_EVENTS = null;

    private static EventData[] DAY_CYCLE = null;

    private static EventData[] QUESTS = null;

    private static EventData[] CONCERTS_SHOWS = null;

    private static EventData[] RESETS = null;

    private static EventData[] SEASONAL_EVENTS = null;

    public static EventData[] getWaxEvents() {
        if (WAX_EVENTS == null) {
            WAX_EVENTS = new EventData[] {
                    new EventData("Polluted Geyser", 2*60*60, (5*60) + 11, 10*60, EventData.TimeType.SKY),
                    new EventData("Grandma's Dinner Event", 2*60*60, 35*60 + 20, 10*60, EventData.TimeType.SKY),
                    new EventData("Sunset Sanctuary Turtle", 2*60*60, 50*60 + 17, 10*60, EventData.TimeType.SKY),
                    new EventData("Dream Skater", new Integer[] {5,6,7}, 2*60*60, 60*60, 10*60, EventData.TimeType.SKY)
            };
        }
        return WAX_EVENTS;
    }

    public static EventData[] getDayCycle() {
        if (DAY_CYCLE == null) {
            DAY_CYCLE = new EventData[] {
                    new EventData("Nest Sunset", 60*60, 40*60, (10*60) + 20, EventData.TimeType.SKY),
                    new EventData("Home Sunrise", 24*60*60, 5*60*60, 4*60*60, EventData.TimeType.LOCAL),
                    new EventData("Home Cloudy", 24*60*60, 9*60*60, 60*60, EventData.TimeType.LOCAL),
                    new EventData("Home Daytime", 24*60*60, 10*60*60, 6*60*60, EventData.TimeType.LOCAL),
                    new EventData("Home Cloudy", 24*60*60, 16*60*60, 60*60, EventData.TimeType.LOCAL),
                    new EventData("Home Sunset", 24*60*60, 17*60*60, 4*60*60, EventData.TimeType.LOCAL),
                    new EventData("Home Nighttime", 24*60*60, 21*60*60, 8*60*60, EventData.TimeType.LOCAL)
            };
        }
        return DAY_CYCLE;
    }

    public static EventData[] getQuests() {
        if (QUESTS == null) {
            QUESTS = new EventData[] {
                    new EventData("Daily Quests", 24*60*60, 0, 24*60*60, EventData.TimeType.SKY),
            };
        }
        return QUESTS;
    }

    public static EventData[] getConcertsShows() {
        if (CONCERTS_SHOWS == null) {
            CONCERTS_SHOWS = new EventData[] {
                    new EventData("Aurora", 4*60*60, (2*60*60)+(10*60), 50*60, EventData.TimeType.SKY)
            };
        }
        return CONCERTS_SHOWS;
    }

    public static EventData[] getResets() {
        if (RESETS == null) {
            RESETS = new EventData[] {
                    new EventData("Daily Reset", 24*60*60, 0, 0, EventData.TimeType.SKY),
                    new EventData("Weekly Reset", new Integer[] {7}, 24*60*60, 0, 0, EventData.TimeType.SKY),
                    new EventData("Passage Quest Reset", 15*60, 0, 0, EventData.TimeType.SKY),
            };
        }
        return RESETS;
    }

    public static EventData[] getSeasonalEvents() {
        if (SEASONAL_EVENTS == null) {
            ZonedDateTime currentTime = SkyClockUtils.getSkyTime();
            SEASONAL_EVENTS = new EventData[] {
                    WikiUtils.getTravellingSpirit(currentTime),
                    WikiUtils.getSeasonEvent(currentTime)
            };
        }
        return SEASONAL_EVENTS;
    }

    private final String name, stringValue;
    private final Function<ZonedDateTime, Long> timeLeft;
    private long cooldown, duration;
    private final TimeType timeType;

    public EventData() {
        this("Empty");
    }

    public EventData(String name) {
        this.name = name;
        stringValue = name + " - Empty Event";
        timeLeft = (time) -> 0L;
        cooldown = 0;
        duration = 0;
        timeType = TimeType.SKY;
    }

    public EventData(String name, long cooldownSeconds, long offsetSeconds, long durationSeconds, TimeType timeType) {
        this.name = name;
        this.stringValue = name + " - cooldown: " + cooldownSeconds + ", offset: " + offsetSeconds + ", duration: " + durationSeconds + ", type: " + timeType;
        this.timeType = timeType;
        this.timeLeft =
                (time) -> {
                    return (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + (time.getDayOfYear()*24*60*60) + ((long) time.getYear() *365*24*60*60)) - (offsetSeconds) + (cooldownSeconds))
                            % (cooldownSeconds);
                };
        this.cooldown = Math.max(cooldownSeconds, 1);
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
        this.stringValue = name + " - days: " + Arrays.toString(days) + "cooldown: " + cooldownSeconds + ", offset: " + offsetSeconds + ", duration: " + durationSeconds + ", type: " + timeType;
        this.timeType = timeType;
        this.timeLeft =
                (time) -> {
                    Integer currentDay = time.getDayOfWeek().getValue();
                    if (onDays.contains(currentDay)) {
                        this.cooldown = cooldownSeconds;
                    } else {
                        Integer lastDay = onDays.getLast();
                        Integer nextDay = onDays.getFirst();
                        for (Integer day : onDays) {
                            if (day > currentDay) {
                                nextDay = day;
                                break;
                            }
                        }
                        lastDay = onDays.get((onDays.indexOf(nextDay) - 1 + onDays.size()) % onDays.size());
                        if (Objects.equals(lastDay, nextDay)) {
                            this.cooldown = 7*24*60*60;
                        } else {
                            this.cooldown = (long) ((lastDay > nextDay ? nextDay : nextDay - lastDay) + 1 % 7) * 24 * 60 * 60;
                        }

                        return Duration.between(time, time.plusDays((nextDay - currentDay + 7) % 7).truncatedTo(ChronoUnit.DAYS)).getSeconds() + offsetSeconds;
                    }
                    return (this.cooldown) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + (time.getDayOfYear()*24*60*60) + ((long) time.getYear() *365*24*60*60)) - (offsetSeconds) + (this.cooldown))
                            % (this.cooldown);

//                    // Stole this from chatgpt (with a couple of tweaks), I'm way too dumb to code this
//                    // Convert ZonedDateTime to day of week (1 = Monday, ..., 7 = Sunday)
//                    int currentDay = time.getDayOfWeek().getValue();
//                    long secondsSinceMidnight = time.getSecond() + (time.getHour() * 60*60);
//                    long secondsUntilMidnight = 24 * 60 * 60 - secondsSinceMidnight;
//
//                    // Check for events on the current day
//                    if (onDays.contains(currentDay)) {
////                        if (onDays.getFirst() == currentDay) {
////                            this.cooldown = (long) (7-onDays.getLast()+onDays.getFirst()) * 24*60*60;
////                            System.out.println("Long");
////                        } else {
////                            this.cooldown = cooldownSeconds;
////                            System.out.println("Short");
////                        }
//                        this.cooldown = cooldownSeconds;
//
//                        long elapsedSinceOffset = Math.max(0, secondsSinceMidnight - offsetSeconds);
//
//                        long nextEventToday = secondsSinceMidnight + cooldownSeconds - (elapsedSinceOffset % cooldownSeconds);
//
//                        if (nextEventToday < 24 * 60 * 60) { // If event still falls on the same day
//                            return cooldownSeconds - (elapsedSinceOffset % cooldownSeconds);
//                        }
//                    }
//
//                    // Find the next available day
//                    this.cooldown = (long) (7-onDays.getLast()+onDays.getFirst()) * 24*60*60;
//                    for (int day : onDays) {
//                        if (day > currentDay) {
//                            return secondsUntilMidnight + (long) ((day - currentDay) - 1) * 24 * 60 * 60 + offsetSeconds;
//                        }
//                    }
//
//                    // If no more events this week, wrap to the first day of the next week
//                    return secondsUntilMidnight + (long) ((7 - currentDay + onDays.getFirst()) - 1) * 24 * 60 * 60 + offsetSeconds;
                };
        this.cooldown = cooldownSeconds;
        this.duration = Math.max(durationSeconds, 10);
    }

    public EventData(String name, ZonedDateTime start, ZonedDateTime end, long cooldownAfterEnd) {
        this.name = name;
        this.stringValue = name + " - starting at: " + start.toString() + ", ending at: " + end.toString() + ", with (theoretical) cooldown " + cooldownAfterEnd + ".";
        this.timeType = TimeType.SKY;
        this.timeLeft = (time) -> {
            if (Duration.between(time, end).isPositive()) {
                this.duration = Duration.between(start, end).getSeconds();
                this.cooldown = Duration.between(start, end.plusSeconds(cooldownAfterEnd)).getSeconds();
                if (Duration.between(time, start).isNegative()) {
                    return Duration.between(time, end.plusSeconds(cooldownAfterEnd)).getSeconds();
                } else {
                    return Duration.between(time, start).getSeconds();
                }
            } else {
                this.duration = 0L;
                this.cooldown = 1L;
                return null;
            }
        };
        this.cooldown = 0;
        this.duration = 10;
    }

    public String getName() {
        return name;
    }

    public Long getTimeLeft(ZonedDateTime[] times) {
        return switch (timeType) {
            case LOCAL -> timeLeft.apply(times[1]);
            default -> timeLeft.apply(times[0]);
        };
//        return null;
    }

    public boolean active(ZonedDateTime[] times) {
        Long timeLeft = getTimeLeft(times);
        if (timeLeft == null) {
            return false;
        }
//        return false;
        return (cooldown - timeLeft) <= duration;
    }

    public float percentElapsed(ZonedDateTime[] times) {
        Long timeLeft = getTimeLeft(times);
        if (timeLeft == null) {
            return 0f;
        }
        if (active(times)) {
            return Math.clamp(1f - ((float) (cooldown - timeLeft) / duration), 0, 1f);
        } else {
            return Math.clamp(((float) ((cooldown - duration) - timeLeft) / (cooldown - duration)), 0, 1f);
        }
    }

    public long durationLeft(ZonedDateTime[] times) {
        return Math.clamp(duration-(cooldown - getTimeLeft(times)), 0, duration);
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventData e) {
            return (e.getName().equals(this.getName())) && (e.timeLeft.equals(this.timeLeft));
        }
        return false;
    }
}
