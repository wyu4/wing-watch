package com.WingWatch;

import com.WingWatch.FrontEnd.App;
import com.WingWatch.FrontEnd.Themes.WingWatchDark;
import com.formdev.flatlaf.FlatLaf;

import java.awt.EventQueue;

public class TestStart {
    public static void main(String[] args) {
        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            WingWatchDark.setup();
        } catch (Exception e) {
            System.err.println("Error registering FlatLaf themes: " + e);
        }
        EventQueue.invokeLater(App::new);
    }
}
