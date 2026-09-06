package com.stockpilot.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class ApiClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api";

    private static final String BASE_URL =
            System.getProperty("stockpilot.api.url", DEFAULT_BASE_URL);

    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

    private ApiClient() {
    }

    public static String get(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        return send(request);
    }

    public static String post(String endpoint, String json)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return send(request);
    }

    public static String put(String endpoint, String json)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return send(request);
    }

    public static void delete(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(15))
                .DELETE()
                .build();

        send(request);
    }

    private static String send(HttpRequest request)
            throws IOException, InterruptedException {

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "API request failed. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }

        return response.body();
    }

    public static String baseUrl() {
        return BASE_URL;
    }
}
