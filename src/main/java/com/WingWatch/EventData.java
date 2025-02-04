package com.WingWatch;

import com.WingWatch.WebScraping.SkyClockUtils;
import com.WingWatch.WebScraping.WikiUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Function;

public class EventData {
    public enum TimeType {
        SKY, LOCAL
    }

    public enum EventState {
        NEXT_IN, ONGOING, LAST_IN, UNKNOWN
    }

    private static final HashMap<String, EventData[]> PRESET_EVENTS = new HashMap<>();

    public static void clearPresets() {
        PRESET_EVENTS.clear();
    }

    private static EventData[] ifPresetNotFoundReturn(String key, EventData[] defaultValue) {
        if (!PRESET_EVENTS.containsKey(key)) {
            PRESET_EVENTS.put(key, defaultValue);
        }
        return PRESET_EVENTS.get(key);
    }

    public static EventData[] getWaxEvents() {
        return ifPresetNotFoundReturn("WAX", new EventData[] {
                new EventData("Polluted Geyser", 2*60*60, 5*60, 10*60, EventData.TimeType.SKY),
                new EventData("Grandma's Dinner Event", 2*60*60, 35*60, 10*60, EventData.TimeType.SKY),
                new EventData("Sunset Sanctuary Turtle", 2*60*60, 50*60, 10*60, EventData.TimeType.SKY),
                new EventData("Dream Skater", new Integer[] {5,6,7}, 2*60*60, 60*60, 10*60, EventData.TimeType.SKY)
        });
    }

    public static EventData[] getDayCycle() {
        return ifPresetNotFoundReturn("DAY_CYCLE", new EventData[] {
                new EventData("Nest Sunset", 60*60, 40*60, (10*60) + 20, EventData.TimeType.SKY),
                new EventData("Home Sunrise", 24*60*60, 5*60*60, 4*60*60, EventData.TimeType.LOCAL),
                new EventData("Home Cloudy Morning", 24*60*60, 9*60*60, 60*60, EventData.TimeType.LOCAL),
                new EventData("Home Daytime", 24*60*60, 10*60*60, 6*60*60, EventData.TimeType.LOCAL),
                new EventData("Home Cloudy Afternoon", 24*60*60, 16*60*60, 60*60, EventData.TimeType.LOCAL),
                new EventData("Home Sunset", 24*60*60, 17*60*60, 4*60*60, EventData.TimeType.LOCAL),
                new EventData("Home Nighttime", 24*60*60, 21*60*60, 8*60*60, EventData.TimeType.LOCAL)
        });
    }

    public static EventData[] getQuests() {
        return ifPresetNotFoundReturn("QUESTS", new EventData[] {
                new EventData("Daily Quests", 24*60*60, 0, 24*60*60, EventData.TimeType.SKY),
        });
    }

    public static EventData[] getConcertsShows() {
        return ifPresetNotFoundReturn("CONCERT_SHOWS", new EventData[] {
                new EventData("Aurora", 4*60*60, (2*60*60)+(10*60), 50*60, EventData.TimeType.SKY),
                new EventData("Fireworks",
                        (time) -> Duration.between(time, ZonedDateTime.of(time.getYear(), time.getMonthValue(), 1, 0, 0, 0, 0, time.getZone()).plusMonths(1)).getSeconds(),
                        (time) -> {
                            ZonedDateTime last = ZonedDateTime.of(time.getYear(), time.getMonthValue(), 1, 0, 0, 0, 0, time.getZone());
                            return Duration.between(last, last.plusMonths(1)).getSeconds();
                        }
                        )
        });
    }

    public static EventData[] getResets() {
        return ifPresetNotFoundReturn("RESETS", new EventData[] {
                new EventData("Daily Reset", 24*60*60, 0, 0, EventData.TimeType.SKY),
                new EventData("Weekly Reset", new Integer[] {7}, 7*24*60*60, 0, 0, EventData.TimeType.SKY),
                new EventData("Music Sheet Reset", new Integer[] {1}, 7*24*60*60, 0, 0, EventData.TimeType.SKY),
                new EventData("Passage Quest Reset", 15*60, 0, 0, EventData.TimeType.SKY),
        });
    }

    public static EventData[] getSeasonalEvents() {
        ZonedDateTime currentTime = SkyClockUtils.getSkyTime();
        if (currentTime == null) {
            return new EventData[0];
        }
        return new EventData[] {
                WikiUtils.getTravellingSpirit(currentTime.getZone()),
                WikiUtils.getSeasonEvent(currentTime.getZone()),
                WikiUtils.getDaysEvent(currentTime.getZone())
        };
    }

    private final String name, stringValue;
    private long cooldown, duration;
    private final TimeType timeType;
    private Function<ZonedDateTime, Long> timeLeft;
    private final Function<ZonedDateTime, Long> timeLeftAfterChecks = (time) -> {
        if (time == null || timeLeft == null) {
            return null;
        }
        return timeLeft.apply(time);
    };

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
                (time) -> (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + (time.getDayOfYear()*24*60*60) + ((long) time.getYear() *365*24*60*60)) - (offsetSeconds) + (cooldownSeconds))
                        % (cooldownSeconds);
        this.cooldown = Math.max(cooldownSeconds, 1);
        this.duration = durationSeconds;
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
                        long next = (cooldownSeconds) - ((time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60) + (time.getDayOfYear()*24*60*60) + ((long) time.getYear() *365*24*60*60)) - (offsetSeconds) + (cooldownSeconds))
                                % (cooldownSeconds);

                        if (onDays.contains(time.plusSeconds(next).getDayOfWeek().getValue())) {
                            this.cooldown = cooldownSeconds;
                            return next;
                        }
                    }
                    Integer lastDay;
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
//                        if (name.equals("Dream Skater")) {
//                            System.out.println(currentDay + ", " + lastDay + " - " + nextDay);
//                        }
                        this.cooldown = (long) ((lastDay > nextDay ? nextDay + (7-lastDay) : nextDay - lastDay) + 1 % 7) * 24 * 60 * 60;
                    }
                    return Duration.between(
                            time,
                            time.plusDays(
                                    ////////////////////////////////////
                                    // Must be changed
                                    currentDay.equals(nextDay) ? 7 : ((nextDay - currentDay + 7) % 7)
                                    ////////////////////////////////////
                            ).truncatedTo(ChronoUnit.DAYS)
                    ).getSeconds() + offsetSeconds;
                };
        this.cooldown = cooldownSeconds;
        this.duration = durationSeconds;
    }

    public EventData(String name, ZonedDateTime start, ZonedDateTime end, long cooldownAfterEnd) {
        this.name = name;
        this.stringValue = name + " - starting at: " + start + ", ending at: " + end + ", with (theoretical) cooldown " + cooldownAfterEnd + ".";
        this.timeType = TimeType.SKY;
        this.timeLeft = (time) -> {
            if (start == null || end == null) {
                return null;
            }
            long currentToEnd = Duration.between(time, end).getSeconds();
            if (currentToEnd >= 0) {
                this.duration = Duration.between(start, end).getSeconds();
                this.cooldown = Duration.between(start, end.plusSeconds(cooldownAfterEnd)).getSeconds();
                if (Duration.between(time, start).getSeconds() <= 0) {
                    return Duration.between(time, end.plusSeconds(cooldownAfterEnd)).getSeconds();
                }
                return Duration.between(time, start).getSeconds();
            } else {
//                System.out.println(name + ": " + Duration.between(time, end).getSeconds());
                return currentToEnd;
//                this.duration = 0L;
//                this.cooldown = 1L;
            }
//            return null;
        };
        this.cooldown = 0;
        this.duration = 0;
    }

    public EventData(String name, Function<ZonedDateTime, Long> timeLeft, Function<ZonedDateTime, Long> calcCooldown) {
        this.name = name;
        this.stringValue = name + " - with custom timeLeft() function.";
        this.timeLeft = (time) -> {
            cooldown = calcCooldown.apply(time);
            return timeLeft.apply(time);
        };
        this.timeType = TimeType.SKY;
        this.cooldown = 0;
        this.duration = 0;
    }

    public String getName() {
        return name;
    }

    public Long getTimeLeft(ZonedDateTime[] times) {
        return switch (timeType) {
            case LOCAL -> timeLeftAfterChecks.apply(times[1]);
            default -> timeLeftAfterChecks.apply(times[0]);
        };
    }

    public EventState calculateState(ZonedDateTime[] times) {
        Long timeLeft = getTimeLeft(times);
        if (timeLeft == null) {
            return EventState.UNKNOWN;
        }
//        if (name.equals("Dream Skater")) {
//            System.out.println(cooldown + " - " + timeLeft + " <= " + duration + " = " + ((cooldown - timeLeft) <= duration));
//        }

        if (timeLeft >= 0 && duration >= 0) {
            if ((cooldown - timeLeft) <= duration) {
                return EventState.ONGOING;
            }
            return EventState.NEXT_IN;
        }
        return EventState.LAST_IN;
    }

//    public boolean active(ZonedDateTime[] times) {
//        return calculateState(times) == EventState.ONGOING;
//    }

    public float percentElapsed(ZonedDateTime[] times) {
        Long timeLeft = getTimeLeft(times);
        if (timeLeft == null || timeLeft < 0) {
            return 0f;
        }
        if (calculateState(times) == EventState.ONGOING) {
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
