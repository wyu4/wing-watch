package com.WingWatch;

import com.WingWatch.FrontEnd.App;
import com.WingWatch.FrontEnd.Themes.WingWatchDark;
import com.formdev.flatlaf.FlatLaf;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;


public class Start {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try {
            File errors = new File("Errors.txt");
            errors.createNewFile();
            System.setErr(new PrintStream(errors));
        } catch (IOException e) {
            System.err.println("Could not create error file: " + e.getMessage());
        }

        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            WingWatchDark.setup();
            EventQueue.invokeLater(App::new);
        } catch (Exception e) {
            System.err.println("Error on startup: " + e);
        }
    }
}
