package com.WingWatch.FrontEnd;

import com.WingWatch.EventData;
import com.WingWatch.WebScraping.SkyClockUtils;
import com.WingWatch.WebScraping.WikiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame implements ActionListener, WindowListener, Refreshable {
    private static List<App> SESSIONS = new ArrayList<>();
    public static Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private ZonedDateTime[] times = new ZonedDateTime[2];
    private final Timer runtime = new Timer(1, this);
    private final JTabbedPane tabs = new JTabbedPane();
    private final GlobalClockDisplay globalClockDisplay = new GlobalClockDisplay();
    private final OffsetTimeSlider offsetTimeSlider = new OffsetTimeSlider();
    private final OrderedSchedule
            waxEvents = new OrderedSchedule(),
            questEvents = new OrderedSchedule(),
            concertShowEvents = new OrderedSchedule(),
            seasonalEvents = new OrderedSchedule(),
            resetEvents = new OrderedSchedule(),
            dayCycleEvents = new OrderedSchedule();
    private final ConfigTab configs = new ConfigTab();
    private Long lastFrame = null;
    private ZonedDateTime testTime1, testTime2;

    public App() {
        super("Sky Events");

        refreshData();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension((int)(SCREEN_SIZE.width/2f), (int)(SCREEN_SIZE.height/2.5f)));
        setMaximumSize(SCREEN_SIZE);
        setPreferredSize(getMinimumSize());
        setSize(getMinimumSize());

        tabs.add("Wax", waxEvents);
        tabs.add("Quests", questEvents);
        tabs.add("Concerts & Shows", concertShowEvents);
        tabs.add("Seasonals", seasonalEvents);
        tabs.add("Resets", resetEvents);
        tabs.add("Day Cycle", dayCycleEvents);
        tabs.add("Extras", configs);

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
    public void refreshData() {
        SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
        SkyClockUtils.refreshData();
        WikiUtils.refreshSources();
        EventData.clearPresets();

        waxEvents.trackEvents(EventData.getWaxEvents());
        questEvents.trackEvents(EventData.getQuests());
        concertShowEvents.trackEvents(EventData.getConcertsShows());
        seasonalEvents.trackEvents(EventData.getSeasonalEvents());
        resetEvents.trackEvents(EventData.getResets());
        dayCycleEvents.trackEvents(EventData.getDayCycle());
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
        long delta = System.currentTimeMillis() - lastFrame;
        float timeMod = ((float) delta) / runtime.getDelay();

        times[0] = offsetTimeSlider.getOffset(SkyClockUtils.getSkyTime());
        times[1] = offsetTimeSlider.getOffset(ZonedDateTime.now());

        OrderedSchedule.stepAll(times, timeMod);
        globalClockDisplay.step(times);
        configs.step();

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

class ConfigTab extends JScrollPane {
    private final JPanel contentPane = new JPanel();

    public ConfigTab() {
        setName("ConfigTab");
        setBorder(null);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        viewport.setName("Viewport");
        viewport.setBackground(new Color(0, 0, 0, 0));
        viewport.setBorder(null);
        viewport.setLayout(null);
        viewport.setDoubleBuffered(true);
        viewport.setLocation(0, 0);
        viewport.setSize(getSize());

        contentPane.setName("ContentPane");
        contentPane.setBackground(new Color(0, 0, 0, 0));

        viewport.setView(contentPane);
    }

    public void step() {
        if (!isVisible()) {
            return;
        }
    }
}