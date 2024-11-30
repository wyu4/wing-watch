import BackendClasses.Events.Events;
import BackendClasses.SkyClock;

import java.time.ZonedDateTime;

public class EventTest {
    public static void main(String[] args) {
        SkyClock.refreshData();
        ZonedDateTime testTime = ZonedDateTime.of(2024, 11, 30, 0, 0, 0, 0, SkyClock.getSkyTime().getZone());
        while (!Thread.interrupted()) {
            int remainingTime = Events.GRANDMA.getSecondsLeftTillNext(SkyClock.getSkyTime());
            System.out.printf("%02d:%02d:%02d:%02d%n", 0, (remainingTime/3600), ((remainingTime%3600)/60), (remainingTime%60));
        }
        System.exit(0);
    }
}
