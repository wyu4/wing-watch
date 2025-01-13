package com.WingWatch.WebScraping;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;

public abstract class LocalCache {
    private static final String FILE_NAME = "cache.json";
    private static final Gson mapper = new Gson();

    private static HashMap<String, String> lastLoaded = new HashMap<>();

    private static String getSource() throws IOException {
        StringBuilder source;

        File sourceFile = new File(FILE_NAME);
        sourceFile.createNewFile();

        try (InputStream input =new FileInputStream(sourceFile)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                source = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line);
                }
            }

        }
        if (source.isEmpty()) {
            return "{}";
        }

        return source.toString();
    }

    private static void setSource(HashMap<String, String> cache) throws IOException {
        String newSource = mapper.toJson(cache);
        File sourceFile = new File(FILE_NAME);
        sourceFile.createNewFile();

        try (PrintStream printer = new PrintStream(sourceFile)) {
            printer.print(newSource);
        }
    }

    private static HashMap<String, String> getCache() {
        if (lastLoaded.isEmpty()) {
            try {
                lastLoaded = mapper.fromJson(getSource(), lastLoaded.getClass());
            } catch (Exception e) {
                System.err.println("Could not load local cache: " + e);
            }
        }
        return lastLoaded;
    }

    public static String getValue(String key) {
        return getCache().get(key);
    }

    public static String getValue(String key, String defaultValue) {
        String current = getCache().get(key);
        if (current != null) {
            return current;
        }
        setValue(key, defaultValue);
        return defaultValue;
    }

    public static void setValue(String key, String value) {
        if (value == null) {
            return;
        }

        HashMap<String, String> cache = getCache();
        cache.put(key, value);

        try {
            setSource(cache);
        } catch (IOException e) {
            System.err.println("Could not save new cache: " + e);
        }
    }
}
