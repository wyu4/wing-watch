package com.WingWatch.FrontEnd;

import com.WingWatch.SkyClock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

public class App extends JFrame implements ActionListener, WindowListener {
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final Timer runtime = new Timer(1, this);
    private final OrderedSchedule eventSchedule = new OrderedSchedule();
    private final GlobalClockDisplay globalClockDisplay = new GlobalClockDisplay();

    private Long lastFrame = null, test = System.currentTimeMillis();
    private ZonedDateTime testTime1, testTime2;

    public App() {
        super("Sky Events");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension((int)(SCREEN_SIZE.width/2.5f), SCREEN_SIZE.height/3));
        setMaximumSize(SCREEN_SIZE);
        setPreferredSize(getMinimumSize());
        setSize(getMinimumSize());

        eventSchedule.trackEvents(SkyClock.WAX_EVENTS);

        addWindowListener(this);
        add(eventSchedule, BorderLayout.CENTER);
        add(globalClockDisplay, BorderLayout.NORTH);

        refreshData();

        setVisible(true);
        revalidate();
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
//        if (testTime1 == null || testTime2 == null) {
//            return;
//        }
//        ZonedDateTime skyTime = null;
//        if ((System.currentTimeMillis() - test) % 10000 > 5000) {
//            skyTime = testTime2;
//        } else {
//            skyTime = testTime1;
//        }
        ZonedDateTime skyTime = SkyClock.getSkyTime();
        long delta = System.currentTimeMillis() - lastFrame;
        float timeMod = (float) delta / runtime.getDelay();

        eventSchedule.step(skyTime, timeMod);
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
