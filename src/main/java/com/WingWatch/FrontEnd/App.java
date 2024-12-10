package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;
import com.WingWatch.SkyClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

public class App extends JFrame implements ActionListener, WindowListener {
    public static boolean SESSION_OPEN = false;

    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final Timer runtime = new Timer(1, this);
    private final JTabbedPane tabs = new JTabbedPane();
    private final GlobalClockDisplay globalClockDisplay = new GlobalClockDisplay();

    private Long lastFrame = null;
    private ZonedDateTime testTime1, testTime2;

    public App() {
        super("Sky Events");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension((int)(SCREEN_SIZE.width/2f), SCREEN_SIZE.height/3));
        setMaximumSize(SCREEN_SIZE);
        setPreferredSize(getMinimumSize());
        setSize(getMinimumSize());

        tabs.add("Wax", new OrderedSchedule(SkyClock.WAX_EVENTS));
        tabs.add("Quests", new OrderedSchedule(SkyClock.QUESTS));
        tabs.add("Resets", new OrderedSchedule(SkyClock.RESETS));
        tabs.add("Day Cycle", new OrderedSchedule(SkyClock.DAY_CYCLE));

        addWindowListener(this);
        add(tabs, BorderLayout.CENTER);
        add(globalClockDisplay, BorderLayout.NORTH);

        refreshData();

        setVisible(true);
        revalidate();

        SESSION_OPEN = true;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void refreshData() {
        EventQueue.invokeLater(() -> {
            try {
                SkyClock.refreshData();
                testTime1 = ZonedDateTime.of(2024, 12, 7, 0, 5, 0, 0, SkyClock.getSkyTime().getZone());
                testTime2 = ZonedDateTime.of(2024, 12, 7, 3, 5, 0, 0, SkyClock.getSkyTime().getZone());
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(runtime)) {
            return;
        }
        if (lastFrame == null) {
            lastFrame = System.currentTimeMillis();
            return;
        }
        ZonedDateTime skyTime = SkyClock.getSkyTime();
        long delta = System.currentTimeMillis() - lastFrame;
        float timeMod = (float) delta / runtime.getDelay();

        OrderedSchedule.stepAll(skyTime, timeMod);
        globalClockDisplay.step(skyTime);

        repaint();
        lastFrame = System.currentTimeMillis();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        EventQueue.invokeLater(runtime::start);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (getDefaultCloseOperation() == EXIT_ON_CLOSE) {
            SESSION_OPEN = false;
        }
        runtime.stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {
        runtime.stop();
        lastFrame = null;
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        EventQueue.invokeLater(runtime::restart);
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
