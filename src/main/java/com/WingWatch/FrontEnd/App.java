package com.WingWatch.FrontEnd;

import com.WingWatch.SkyClock;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame implements ActionListener, WindowListener {
    private static List<App> SESSIONS = new ArrayList<>();

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

        SESSIONS.add(this);
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
        if (!SESSIONS.getFirst().equals(this)) {
            System.err.println("Only one session at a time.");
            closeFrame();
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
        SESSIONS.remove(this);
        runtime.stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (SESSIONS.isEmpty()) {
            System.exit(0);
        }
    }

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

    private void closeFrame() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
