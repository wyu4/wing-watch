package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class OrderedSchedule extends JPanel {
    private EventData[] orderedEvents;
    private final HashMap<EventData, ScheduleComponent> schedule = new HashMap<>();

    public OrderedSchedule() {
        setName("OrderedSchedule");
        setLayout(null);
        setDoubleBuffered(true);
    }

    public void trackEvents(EventData[] events) {
        this.orderedEvents = events;
        for (EventData event : events) {
            schedule.put(event, getScheduleComponentOf(event));
        }
    }

    private ScheduleComponent getScheduleComponentOf(EventData event) {
        if (schedule.containsKey(event)) {
            return schedule.get(event);
        }
        return new ScheduleComponent(event);
    }

    private void sortEvents(ZonedDateTime skyTime) {
        // Bubble Sort cuz why not
        final int n = orderedEvents.length-1;
        EventData temp;
        for (int i = 0; i < n; i++) {
            boolean sorted = true;
            for (int j = 0; j < n - i; j++) {
                if (orderedEvents[j].getTimeLeft(skyTime) > orderedEvents[j + 1].getTimeLeft(skyTime)) {
                    temp = orderedEvents[j];
                    orderedEvents[j] = orderedEvents[j + 1];
                    orderedEvents[j + 1] = temp;
                    sorted = false;
                }
            }
            if (sorted) {
                break;
            };
        }
    }

    public void step(ZonedDateTime skyTime, float timeMod) {
        sortEvents(skyTime);
        Component[] componentsToShow = new ScheduleComponent[orderedEvents.length];
        Component[] componentsShown = getComponents();
        for (int i = 0; i < orderedEvents.length; i++) {
            ScheduleComponent comp = getScheduleComponentOf(orderedEvents[i]);
            comp.setIndex(i);
            comp.step(skyTime, timeMod);
            componentsToShow[i] = comp;
        }
        for (Component comp : componentsShown) {
            if (!Arrays.asList(componentsToShow).contains(comp)) {
                remove(comp);
                System.out.println("Removed " + comp);
            }
        }
        for (Component comp : componentsToShow) {
            if (!Arrays.asList(componentsShown).contains(comp)) {
                add(comp);
                System.out.println("Added " + comp);
            }
        }
    }

    @Override
    public String toString() {
        if (orderedEvents == null || orderedEvents.length == 0) {
            return "OrderedSchedule:{}";
        }
        StringBuilder result = new StringBuilder("OrderedSchedule:{");
        for (int i = 0; i < orderedEvents.length; i++) {
            result.append(" [").append(i).append("]:").append(orderedEvents[i]);
            if (i < orderedEvents.length-1) {
                result.append(",");
            }
            result.append(" ");
        }
        return result.append("}").toString();
    }
}
class ScheduleComponent extends JPanel {
    private final EventData linkedData;
    private final JLabel indexLabel = new JLabel();
    private int index = 0;

    public ScheduleComponent(EventData linkedData) {
        this.linkedData = linkedData;

        setName("ScheduleComponent-" + linkedData.getName());
        setBackground(UIManager.getColor("Schedule.PadColor"));
        setLayout(null);
        setDoubleBuffered(true);

        add(indexLabel);
    }

    public EventData getLinkedData() {
        return linkedData;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void resizeBasedOnParent(Component parent) {
        setSize(parent.getWidth(), App.SCREEN_SIZE.height/20);
    }

    private void moveToIndexLocation(Component parent, float timeMod) {
        int desiredY = (index * getHeight()) + ((index+1)*5);
        double increment = App.SCREEN_SIZE.height*0.0003*timeMod;

        if (desiredY - getY() > 0) {
            setLocation(0, (int) Math.clamp(getY() + increment, getY(), desiredY));
        } else if (desiredY - getY() < 0) {
            setLocation(0, (int) Math.clamp(getY() - increment, desiredY, getY()));
        } else {
            setLocation(0, desiredY);
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

    public void step(ZonedDateTime skyTime, float timeMod) {
        Component parent = getParent();
        if (parent != null) {
            resizeBasedOnParent(parent);
            moveToIndexLocation(parent, timeMod);
            indexLabel.setBounds(0, 0, getWidth(), getHeight());
            indexLabel.setText(linkedData.getName()  + " - " + formatTimeLeft(linkedData.getTimeLeft(skyTime)));
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}