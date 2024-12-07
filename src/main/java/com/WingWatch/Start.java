package com.WingWatch;

import com.WingWatch.FrontEnd.App;
import com.WingWatch.FrontEnd.Themes.WingWatchDark;
import com.formdev.flatlaf.FlatLaf;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Start {
    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("themes");
        WingWatchDark.setup();
        EventQueue.invokeLater(() -> {
            try {
                SkyClock.refreshData();
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        EventQueue.invokeLater(App::new);
    }
}
