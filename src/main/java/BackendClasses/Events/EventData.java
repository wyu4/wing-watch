package BackendClasses.Events;

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
