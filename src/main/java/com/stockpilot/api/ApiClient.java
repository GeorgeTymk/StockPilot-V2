package com.stockpilot.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE_URL =
            "http://localhost:8080/api";

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    // =====================================================
    // GET
    // =====================================================

    public static String get(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                BASE_URL + endpoint
                        ))
                        .GET()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        checkResponse(response);

        return response.body();
    }

    // =====================================================
    // POST
    // =====================================================

    public static String post(
            String endpoint,
            String json
    ) throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                BASE_URL + endpoint
                        ))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        checkResponse(response);

        return response.body();
    }

    // =====================================================
    // PUT
    // =====================================================

    public static String put(
            String endpoint,
            String json
    ) throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                BASE_URL + endpoint
                        ))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        checkResponse(response);

        return response.body();
    }

    // =====================================================
    // DELETE
    // =====================================================

    public static void delete(String endpoint)
            throws IOException, InterruptedException {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                BASE_URL + endpoint
                        ))
                        .DELETE()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        checkResponse(response);
    }

    // =====================================================
    // RESPONSE CHECK
    // =====================================================

    private static void checkResponse(
            HttpResponse<String> response
    ) {

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new RuntimeException(
                    "API request failed. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body()
            );
        }
    }
}