package com.WingWatch.FrontEnd;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class GlobalClockDisplay extends JPanel {
    private final JLabel skyTitle = new JLabel("Sky Time", SwingConstants.CENTER), localTitle = new JLabel("Local Time", SwingConstants.CENTER);
    private final JLabel skyLabel = new JLabel("???", SwingConstants.CENTER), localLabel = new JLabel("???",  SwingConstants.CENTER);

    public GlobalClockDisplay() {
        setName("GlobalClock");
        setDoubleBuffered(true);
        setLayout(new GridLayout(2, 2));

        skyLabel.setDoubleBuffered(true);
        localLabel.setDoubleBuffered(true);

        add(skyTitle);add(localTitle);
        add(skyLabel);add(localLabel);
    }

    public void step(ZonedDateTime[] times) {
        int titleSize = (int)(App.SCREEN_SIZE.width * 0.015f);
        int timeSize = (int)(App.SCREEN_SIZE.width * 0.01f);

        skyTitle.setFont(new Font(skyTitle.getFont().getName(), Font.BOLD, titleSize));
        localTitle.setFont(new Font(localTitle.getFont().getName(), Font.BOLD, titleSize));

        skyLabel.setFont(new Font(skyLabel.getFont().getName(), skyLabel.getFont().getStyle(), timeSize));
        localLabel.setFont(new Font(localLabel.getFont().getName(), localLabel.getFont().getStyle(), timeSize));

        skyLabel.setText(formatTime(times[0]));
        localLabel.setText(formatTime(times[1]));
    }

    private String formatTime(ZonedDateTime time) {
        if (time == null) {
            return "???";
        }
        return String.format("%04d/%02d/%02d  %02d:%02d:%02d",
                time.getYear(),
                time.getMonth().getValue(),
                time.getDayOfMonth(),
                time.getHour(),
                time.getMinute(),
                time.getSecond()
        );
    }
}
