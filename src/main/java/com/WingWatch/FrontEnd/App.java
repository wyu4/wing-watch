package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;
import com.WingWatch.SkyClockUtils;
import com.WingWatch.WikiUtils;

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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame implements ActionListener, WindowListener {
    private static List<App> SESSIONS = new ArrayList<>();
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private ZonedDateTime[] times = new ZonedDateTime[2];
    private final Timer runtime = new Timer(1, this);
    private final JTabbedPane tabs = new JTabbedPane();
    private final GlobalClockDisplay globalClockDisplay = new GlobalClockDisplay();
    private final OffsetTimeSlider offsetTimeSlider = new OffsetTimeSlider();

    private Long lastFrame = null;
    private ZonedDateTime testTime1, testTime2;

    public App() {
        super("Sky Events");

        SkyClockUtils.refreshData();
        WikiUtils.refreshSources();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension((int)(SCREEN_SIZE.width/2f), SCREEN_SIZE.height/3));
        setMaximumSize(SCREEN_SIZE);
        setPreferredSize(getMinimumSize());
        setSize(getMinimumSize());

        tabs.add("Wax", new OrderedSchedule(EventData.getWaxEvents()));
        tabs.add("Quests", new OrderedSchedule(EventData.getQuests()));
        tabs.add("Concerts & Shows", new OrderedSchedule(EventData.getConcertsShows()));
        tabs.add("Seasonals", new OrderedSchedule(EventData.getSeasonalEvents()));
        tabs.add("Resets", new OrderedSchedule(EventData.getResets()));
        tabs.add("Day Cycle", new OrderedSchedule(EventData.getDayCycle()));

        addWindowListener(this);
        add(tabs, BorderLayout.CENTER);
        add(globalClockDisplay, BorderLayout.NORTH);
        add(offsetTimeSlider, BorderLayout.SOUTH);

        setVisible(true);
        requestFocus();
        revalidate();

        SESSIONS.add(this);
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
            SESSIONS.getFirst().closeFrame();
        }
        times[0] = offsetTimeSlider.getOffset(SkyClockUtils.getSkyTime());
        times[1] = offsetTimeSlider.getOffset(ZonedDateTime.now());
//        times[0] = ZonedDateTime.of(2024, 12, 24, 0, 0, 0, 0, SkyClock.getSkyTime().getZone());
        long delta = System.currentTimeMillis() - lastFrame;
        float timeMod = ((float) delta) / runtime.getDelay();

        OrderedSchedule.stepAll(times, timeMod);
        globalClockDisplay.step(times);

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
