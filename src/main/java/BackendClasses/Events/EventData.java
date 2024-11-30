package BackendClasses.Events;

import BackendClasses.SkyClock;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.function.Function;

public class EventData {
    private String name = "unnamed";
    private Function<Integer, Integer> daysLeft = (days) -> 0, hoursLeft = (hours) -> 0, minutesLeft = (minutes) -> 0, secondsLeft = (seconds) -> 0;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTimeLeft(
            Function<Integer, Integer> daysLeft,
            Function<Integer, Integer> hoursLeft,
            Function<Integer, Integer> minutesLeft,
            Function<Integer, Integer> secondsLeft
    ) {
        this.daysLeft = daysLeft;
        this.hoursLeft = hoursLeft;
        this.minutesLeft = minutesLeft;
        this.secondsLeft = secondsLeft;
    }

    public int[] getTimeLeft(ZonedDateTime skyTime) {
        Integer days = daysLeft.apply(skyTime.getDayOfWeek().getValue());
        Integer hours = hoursLeft.apply(skyTime.getHour());
        Integer minutes = minutesLeft.apply(skyTime.getMinute());
        Integer seconds = secondsLeft.apply(skyTime.getSecond());

        return new int[] {
                days != null ? days : 0,
                hours != null ? hours : 0,
                minutes != null ? minutes : 0,
                seconds != null ? seconds : 0,
        };
    }

    @Override
    public String toString() {
        return name + ": " + Arrays.toString(getTimeLeft(SkyClock.getSkyTime()));
    }
}
