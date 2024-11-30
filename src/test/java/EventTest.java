import BackendClasses.Events.Events;

public class EventTest {
    public static void main(String[] args) {
        while (!Thread.interrupted()) {
            System.out.println(Events.WAX_EVENTS);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
