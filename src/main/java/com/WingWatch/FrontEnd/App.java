package com.WingWatch.FrontEnd;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame implements ActionListener {
    private final Timer runtime = new Timer(1, this);

    public App() {
        super("Wing Watch SNAPSHOT");
        setBounds(100, 100, 1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}
