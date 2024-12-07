package com.WingWatch.FrontEnd;

import com.WingWatch.SkyClock;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.*;
import java.time.ZonedDateTime;

public class App extends JFrame implements ActionListener, WindowListener {
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final Timer runtime = new Timer(1, this);
    private final OrderedSchedule eventSchedule = new OrderedSchedule();
    private Long lastFrame = null;

    public App() {
        super("Wing Watch SNAPSHOT");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(SCREEN_SIZE.width/3, SCREEN_SIZE.width/3);

        eventSchedule.trackEvents(SkyClock.WAX_EVENTS);

        addWindowListener(this);
        add(eventSchedule, BorderLayout.CENTER);

        setVisible(true);
        revalidate();
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
//        ZonedDateTime skyTime = ZonedDateTime.of(2024, 12, 7, 3, 5, 0, 0, SkyClock.getSkyTime().getZone());
        ZonedDateTime skyTime = SkyClock.getSkyTime();
        long delta = System.currentTimeMillis() - lastFrame;

        eventSchedule.refreshData(skyTime);

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
