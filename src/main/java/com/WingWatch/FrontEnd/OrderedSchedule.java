package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;
import com.WingWatch.SkyClock;

import javax.swing.JPanel;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class OrderedSchedule extends JPanel {
    private EventData[] events;

    public OrderedSchedule() {
        setName("OrderedSchedule");
    }

    public void trackEvents(EventData[] events) {
        this.events = events;
    }

    private void sortEvents(ZonedDateTime skyTime) {
        // Bubble Sort cuz why not
        final int n = events.length-1;
        EventData temp;
        for (int i = 0; i < n; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i; j++) {
                if (events[j].getTimeLeft(skyTime) > events[j + 1].getTimeLeft(skyTime)) {
                    temp = events[j];
                    events[j] = events[j + 1];
                    events[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            };
        }
    }

    private String formatTimeLeft(int remainingTime) {
        return String.format("%02d:%02d:%02d:%02d",
                        remainingTime/(60*60*24),
                        remainingTime/(60*60) % 24,
                        remainingTime/(60) % 60,
                        remainingTime % 60
                );
    }

    public void refreshData(ZonedDateTime skyTime) {
        sortEvents(skyTime);

    }

    @Override
    public String toString() {
        if (events == null) {
            return "OrderedSchedule:{}";
        }
        StringBuilder result = new StringBuilder("OrderedSchedule:{");
        for (int i = 0; i < events.length; i++) {
            result.append(" [").append(i).append("]:").append(events[i]);
            if (i < events.length-1) {
                result.append(",");
            }
            result.append(" ");
        }
        return result.append("}").toString();
    }
}
