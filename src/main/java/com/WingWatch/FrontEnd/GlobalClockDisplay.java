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
    private final JLabel skyLabel = new JLabel("00 : 00 : 00 : 00", SwingConstants.CENTER), localLabel = new JLabel("00 : 00 : 00 : 00",  SwingConstants.CENTER);

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

        skyLabel.setText(String.format("%04d/%02d/%02d  %02d:%02d:%02d",
                times[0].getYear(),
                times[0].getMonth().getValue(),
                times[0].getDayOfMonth(),
                times[0].getHour(),
                times[0].getMinute(),
                times[0].getSecond()
                ));
        localLabel.setText(String.format("%04d/%02d/%02d  %02d:%02d:%02d",
                times[1].getYear(),
                times[1].getMonth().getValue(),
                times[1].getDayOfMonth(),
                times[1].getHour(),
                times[1].getMinute(),
                times[1].getSecond()
        ));
    }
}
