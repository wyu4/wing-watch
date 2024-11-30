import BackendClasses.Events.Events;
import BackendClasses.SkyClock;

public class EventTest {
    public static void main(String[] args) {
        SkyClock.refreshData();
        while (!Thread.interrupted()) {
            System.out.println(Events.GEYSER);
        }
        System.exit(0);
    }
}
