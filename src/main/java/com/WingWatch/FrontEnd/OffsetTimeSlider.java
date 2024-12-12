package com.WingWatch.FrontEnd;

import com.WingWatch.SkyClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;

public class OffsetTimeSlider extends JPanel implements ActionListener {
    private final JSlider slider = new JSlider();
    private final JLabel timeLabel = new JLabel("+/-00:00:00:00");
    private final JButton stepBackwards = new JButton("<"), resetButton = new JButton("0"), stepForward = new JButton(">");

    public OffsetTimeSlider() {
        setName("TimeSlider");
        setBorder(null);
        setDoubleBuffered(true);
        setLayout(new GridBagLayout());

        stepBackwards.addActionListener(this);
        stepBackwards.setFocusPainted(false);

        resetButton.addActionListener(this);
        resetButton.setFocusPainted(false);

        stepForward.addActionListener(this);
        stepForward.setFocusPainted(false);

        slider.setMinimum(-24*60*60);
        slider.setMaximum(24*60*60);
        slider.setValue(0);
        slider.setFocusable(false);

        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 2;
        constraints.gridx = 1; constraints.gridy = 1;
        constraints.insets = new Insets(0, (int)(App.SCREEN_SIZE.width*0.005), 0, 0);
        add(slider, constraints);

        constraints.gridx = 2; constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.insets = new Insets(0, constraints.insets.left, 0, constraints.insets.left);
        add(timeLabel, constraints);

        constraints.gridx = 3; constraints.gridy = 1;
        constraints.insets = new Insets(0, constraints.insets.left, 0, constraints.insets.left);
        add(stepBackwards, constraints);

        constraints.gridx = 4; constraints.gridy = 1;
        constraints.insets = new Insets(0, constraints.insets.left, 0, constraints.insets.left);
        add(resetButton, constraints);

        constraints.gridx = 5; constraints.gridy = 1;
        constraints.insets = new Insets(0, constraints.insets.left, 0, constraints.insets.left);
        add(stepForward, constraints);
    }

    public ZonedDateTime getOffset(ZonedDateTime time) {
        int offsetSeconds = slider.getValue();
        if (offsetSeconds == 0) {
            timeLabel.setText("+/-00:00:00:00");
            resetButton.setVisible(false);
        } else {
            resetButton.setVisible(true);
            String formattedOffset = SkyClock.formatTimeLeft((long) Math.abs(offsetSeconds));
            timeLabel.setText(offsetSeconds > 0 ? "+" + formattedOffset : "-" + formattedOffset);
        }
        return time.plusSeconds(offsetSeconds);
    }

    private void step(int stepSize) {
        if (!slider.getValueIsAdjusting()) {
            slider.setValue(slider.getValue()+stepSize);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(resetButton)) {
            slider.setValue(0);
        } else if (e.getSource().equals(stepForward)) {
            step(1);
        } else if (e.getSource().equals(stepBackwards)) {
            step(-1);
        }
    }
}
