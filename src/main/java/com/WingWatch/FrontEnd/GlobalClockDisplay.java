package com.WingWatch.FrontEnd;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class GlobalClockDisplay extends JPanel {
    private final JLabel skyTitle = new JLabel("Sky Time", SwingConstants.CENTER), localTitle = new JLabel("Local Time", SwingConstants.CENTER);
    private final JLabel skyLabel = new JLabel("00 : 00 : 00 : 00", SwingConstants.CENTER), localLabel = new JLabel("00 : 00 : 00 : 00",  SwingConstants.CENTER);

    public GlobalClockDisplay() {
        setName("GlobalClock");
        setDoubleBuffered(true);
        setLayout(new GridLayout(2, 2));

        add(skyTitle);add(localTitle);
        add(skyLabel);add(localLabel);
    }

    public void step(ZonedDateTime skyTime) {
        int titleSize = (int)(App.SCREEN_SIZE.width * 0.015f);
        int timeSize = (int)(App.SCREEN_SIZE.width * 0.01f);

        skyTitle.setFont(new Font(skyTitle.getFont().getName(), Font.BOLD, titleSize));
        localTitle.setFont(new Font(localTitle.getFont().getName(), Font.BOLD, titleSize));

        skyLabel.setFont(new Font(skyLabel.getFont().getName(), skyLabel.getFont().getStyle(), timeSize));
        localLabel.setFont(new Font(localLabel.getFont().getName(), localLabel.getFont().getStyle(), timeSize));

        skyLabel.setText(String.format("%04d/%02d/%02d  %02d:%02d:%02d",
                    skyTime.getYear(),
                    skyTime.getMonth().getValue(),
                    skyTime.getDayOfMonth(),
                    skyTime.getHour(),
                    skyTime.getMinute(),
                    skyTime.getSecond()
                ));
        LocalDateTime localTime = LocalDateTime.now();
        localLabel.setText(String.format("%04d/%02d/%02d  %02d:%02d:%02d",
                localTime.getYear(),
                localTime.getMonth().getValue(),
                localTime.getDayOfMonth(),
                localTime.getHour(),
                localTime.getMinute(),
                localTime.getSecond()
        ));
    }
}
