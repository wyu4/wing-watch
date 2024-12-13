package com.WingWatch.WebScraping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Requests {
    private static InputStream getInputStream(String targetUrl) throws IOException, URISyntaxException {
        URL url;
        HttpURLConnection connection;

        // Create a URL Object
        url = new URI(targetUrl).toURL();

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Set the method to s GET Request

        return connection.getInputStream();
    }

    public static StringBuilder requestGetString(String targetUrl) throws IOException, URISyntaxException {
        final StringBuilder result = new StringBuilder();
        InputStream stream = getInputStream(targetUrl);

        // Perform the request
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for (String line; (line = reader.readLine()) != null; ) {
            result.append(line);
        }
        stream.close();
        return result;
    }
}