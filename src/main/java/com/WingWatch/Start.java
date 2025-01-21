package com.WingWatch;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;


public class Start {
    public static void main(String[] args) {
        try {
            File errors = new File("Errors.txt");
            errors.createNewFile();
            System.setErr(new PrintStream(errors));
        } catch (IOException e) {
            System.err.println("Could not create error file: " + e.getMessage());
        }

        TestStart.main(args);
    }
}
