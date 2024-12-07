package com.WingWatch;

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
        return result.append("\nconst error = 'None';");
    }

//    public static BufferedImage requestGetImage(InputStream stream) throws IOException {
//        return ImageIO.read(stream);
//    }

//    public static String requestPost(String serviceUrl, String body) {
//        URI uri;
//        try {
//            uri = URI.create(serviceUrl);
//        } catch(IllegalStateException e) {
//            System.err.println("Could not create URI for POST  for \"" + serviceUrl + "\": " + e.getMessage());
//            throw new IllegalStateException(e);
//        }
//
//        try (HttpClient client = HttpClient.newHttpClient()) {
//            HttpRequest.Builder builder = HttpRequest.newBuilder();
//            HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
//            HttpRequest request = builder.uri(uri)
//                    .setHeader("accept", "application/json")
//                    .setHeader("Content-Type", "application/json")
//                    .POST(publisher)
//                    .build();
//            /////////////////////////////////////////////////////////////////////////////////
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            return response.body();
//            /////////////////////////////////////////////////////////////////////////////////
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}