package BackendClasses.Events;

import BackendClasses.SkyClock;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EventData {
    private String name = "unnamed";
    private Function<ZonedDateTime, Integer> secondsLeftTillNext = (_) -> 0;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTimeLeftTillNext(
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
