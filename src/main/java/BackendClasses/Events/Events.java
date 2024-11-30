package BackendClasses.Events;

import java.util.ArrayList;
import java.util.List;

public class Events {
    public static final String N_GEYSER = "Polluted Geyser";
    public static final String N_GRANDMA = "Grandma's Dinner Event";
    public static final String N_TURTLE = "Sunset Sanctuary Turtle";
    public static final String N_SKATER = "Dream Skater";

    public static final List<EventData> WAX_EVENTS = new ArrayList<>();

    public static final EventData GEYSER = createWaxEvent(N_GEYSER, 2*60, 5);
    public static final EventData GRANDMA = createWaxEvent(N_GRANDMA, 2*60, 35);

    private static EventData createWaxEvent(String name, int cooldownMinutes, int minutesOffset) {
        EventData waxEvent = new EventData();
        waxEvent.setName(name);
        waxEvent.setTimeLeftTillNext(
                (time) -> {
                    int currentTime = time.getSecond() + (time.getMinute()*60) + (time.getHour()*60*60);
                    return (cooldownMinutes*60) - (currentTime - (minutesOffset*60) + (cooldownMinutes*60)) % (cooldownMinutes*60);
                }
        );
        WAX_EVENTS.add(waxEvent);
        return waxEvent;
    }
}
