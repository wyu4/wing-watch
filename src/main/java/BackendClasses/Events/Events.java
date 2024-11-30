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

    private static EventData createWaxEvent(String name, int cooldownMinutes, int minutesOffset) {
        System.out.println("Creating wax event " + name);
        EventData waxEvent = new EventData();
        waxEvent.setName(name);
        waxEvent.setTimeLeft(
                (_) -> 0,
                (h) -> ((h + (minutesOffset % 60)) % (cooldownMinutes / 60)),
                (m) -> ((60 + (minutesOffset % 10) - m) % 60),
                (s) -> ((60 - s) % 60)
        );
        WAX_EVENTS.add(waxEvent);
        return waxEvent;
    }
}
