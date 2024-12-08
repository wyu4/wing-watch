package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;
import com.WingWatch.SkyClock;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class OrderedSchedule extends JScrollPane {
    private final HashMap<EventData, EventDisplay> schedule = new HashMap<>();
    private final JViewport viewport = new JViewport();
    private final JLayeredPane contentPane = new JLayeredPane();
    private EventData[] orderedEvents;

    public OrderedSchedule() {
        setName("OrderedSchedule");
        setDoubleBuffered(true);
        setBorder(null);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        viewport.setName("Viewport");
        viewport.setBackground(new Color(0, 0, 0, 0));
        viewport.setBorder(null);
        viewport.setLayout(null);

        contentPane.setDoubleBuffered(true);
        contentPane.setBackground(new Color(0, 0, 0, 0));
        viewport.setView(contentPane);
        setViewport(viewport);
    }

    public void trackEvents(EventData[] events) {
        this.orderedEvents = events;
        for (EventData event : events) {
            schedule.put(event, getScheduleComponentOf(event));
        }
    }

    private EventDisplay getScheduleComponentOf(EventData event) {
        if (schedule.containsKey(event)) {
            return schedule.get(event);
        }
        return new EventDisplay(event);
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
        Component[] componentsToShow = new EventDisplay[orderedEvents.length];
        Component[] componentsShown = contentPane.getComponents();
        EventDisplay lastDisplay = null;
        for (int i = 0; i < orderedEvents.length; i++) {
            EventDisplay comp = getScheduleComponentOf(orderedEvents[i]);
            comp.setIndex(i);
            comp.step(skyTime, timeMod);
            componentsToShow[i] = comp;
            if (i == orderedEvents.length-1) {
                lastDisplay = comp;
            }
        }
        for (Component comp : componentsShown) {
            if (!Arrays.asList(componentsToShow).contains(comp)) {
                contentPane.remove(comp);
                System.out.println("Removed " + comp);
            }
        }
        for (int i =0; i < componentsToShow.length; i++) {
            Component comp = componentsToShow[i];
            if (!Arrays.asList(componentsShown).contains(comp)) {
                contentPane.add(comp, componentsToShow.length-i, 0);
                System.out.println("Added " + comp);
            } else {
                contentPane.setLayer(comp, componentsToShow.length-i);
            }
        }

        viewport.setLocation(0, 0);
        viewport.setSize(getSize());

        if (lastDisplay != null) {
            contentPane.setSize(getVerticalScrollBar().isVisible() ? getWidth()-getVerticalScrollBar().getWidth() : getWidth(), lastDisplay.calcDesiredY() + lastDisplay.getHeight());
        }
        contentPane.setPreferredSize(contentPane.getSize());
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

class EventDisplay extends JPanel {
    private final ContentPanel contentPanel = new ContentPanel();
    private final EventData linkedData;
    private int index = 0;

    public EventDisplay(EventData linkedData) {
        this.linkedData = linkedData;

        setName("EventDisplay-" + linkedData.getName());
        setBackground(UIManager.getColor("Schedule.PadColor"));
        setLayout(null);
        setDoubleBuffered(true);

        add(contentPanel);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private void resizeBasedOnParent(Component parent) {
        setSize(parent.getWidth(), App.SCREEN_SIZE.height/20);
    }

    public int calcDesiredY() {
        return (index * getHeight()) + ((index + 1) * (int)(App.SCREEN_SIZE.width*0.005)/2);
    }

    private void moveToIndexLocation(Component parent, float timeMod) {
        int desiredY = calcDesiredY();
        double increment = App.SCREEN_SIZE.height*0.0003*timeMod;

        if (desiredY - getY() > 0) {
            setLocation(0, (int) Math.clamp(getY() + increment, getY(), desiredY));
        } else if (desiredY - getY() < 0) {
            setLocation(0, (int) Math.clamp(getY() - increment, desiredY, getY()));
        } else {
            setLocation(0, desiredY);
        }
    }

    public void step(ZonedDateTime skyTime, float timeMod) {
        Component container = getParent();
        if (container == null) {
            return;
        }

        resizeBasedOnParent(container);
        moveToIndexLocation(container, timeMod);

        contentPanel.setBounds(
                (int)(App.SCREEN_SIZE.width*0.005)/2,0,
                getWidth()-((int)(App.SCREEN_SIZE.width*0.005)), getHeight()
        );
        contentPanel.updateContent(
                index,
                linkedData.getName(),
                SkyClock.formatTimeLeft(linkedData.getTimeLeft(skyTime)),
                linkedData.active(skyTime),
                linkedData.percentElapsed(skyTime)
        );
    }

    @Override
    public String toString() {
        return getName();
    }
}

class ContentPanel extends JPanel {
    private final JLabel indexLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel nameLabel = new JLabel("?", SwingConstants.LEFT);
    private final JLabel timeLabel = new JLabel("00:00:00:00", SwingConstants.RIGHT);
    private final JProgressBar progress = new JProgressBar();

    public ContentPanel() {
        setName("ContentPanel");
        putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: $Schedule.ContentBackground;");
        setDoubleBuffered(true);
        setLayout(new GridBagLayout());

        progress.setMinimum(0);
        progress.setMaximum(1000);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0;
        constraints.gridx = 1; constraints.gridy = 1;
        constraints.insets = new Insets(0, (int)(App.SCREEN_SIZE.width*0.005), 0, 0);
        add(indexLabel, constraints);

        constraints.weightx = 1.0;
        constraints.gridx = 2; constraints.gridy = 1;
        add(nameLabel, constraints);

        constraints.weightx = 0;
        constraints.gridx = 3; constraints.gridy = 1;
        add(timeLabel, constraints);

        constraints.gridx = 4; constraints.gridy = 1;
        constraints.insets = new Insets(0, constraints.insets.left, 0, constraints.insets.left);
        add(progress, constraints);
    }

    public void updateContent(
            int index,
            String eventName,
            String time,
            boolean active,
            float percent
    ) {
        int desiredFontSize = (int) (getHeight() * 0.3f);

        indexLabel.setText(String.valueOf(index));
        indexLabel.setFont(new Font(indexLabel.getFont().getName(), Font.BOLD, desiredFontSize));

        nameLabel.setText(eventName);
        nameLabel.setFont(new Font(nameLabel.getFont().getName(), nameLabel.getFont().getStyle(), (int) (desiredFontSize * 1.25f)));

        timeLabel.setText(time);
        timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.BOLD, (int) (desiredFontSize * 1.1f)));

        if (active) {
            progress.setForeground(new Color(0, 255, 0));
        } else {
            progress.setForeground(new Color(47, 69, 89));
        }
        progress.setValue((int)(progress.getMaximum()*percent));
        progress.setString("%" + percent);
    }

}