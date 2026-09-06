package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stockpilot.api.ApiClient;

public class AuthService {

    private final Gson gson = new Gson();

    public boolean authenticate(String username, String password) {

        try {

            JsonObject request = new JsonObject();
            request.addProperty("username", username);
            request.addProperty("password", password);

            String response =
                    ApiClient.post(
                            "/auth/login",
                            gson.toJson(request)
                    );

            JsonObject json =
                    gson.fromJson(response, JsonObject.class);

            boolean success =
                    json != null
                            && json.has("success")
                            && json.get("success").getAsBoolean();

            if (success) {

                String loggedInUser =
                        json.has("username")
                                ? json.get("username").getAsString()
                                : username;

                String role =
                        json.has("role")
                                ? json.get("role").getAsString()
                                : "";

                System.out.println(
                        "API login successful: "
                                + loggedInUser
                                + " ("
                                + role
                                + ")"
                );

            } else {

                System.out.println(
                        json != null && json.has("message")
                                ? json.get("message").getAsString()
                                : "Invalid username or password"
                );
            }

            return success;

        } catch (Exception e) {

            System.err.println(
                    "Unable to connect to StockPilot API at "
                            + ApiClient.baseUrl()
            );

            e.printStackTrace();

            return false;
        }
    }
}
