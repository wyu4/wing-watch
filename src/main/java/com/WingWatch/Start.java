package com.WingWatch;

import com.WingWatch.FrontEnd.App;
import com.WingWatch.FrontEnd.Themes.WingWatchDark;
import com.formdev.flatlaf.FlatLaf;

import java.awt.*;

public class Start {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        WingWatchDark.setup();
        EventQueue.invokeLater(App::new);
    }
}
