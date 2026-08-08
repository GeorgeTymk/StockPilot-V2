package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.model.Purchase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PurchaseService {

    private final Gson gson = new Gson();

    // =====================================================
    // Get all purchases
    // GET /api/purchases
    // =====================================================

    public List<Purchase> getAllPurchases() {

        try {

            String json =
                    ApiClient.get("/purchases");

            Type listType =
                    new TypeToken<List<Purchase>>() {}.getType();

            List<Purchase> purchases =
                    gson.fromJson(json, listType);

            if (purchases == null) {
                return new ArrayList<>();
            }

            System.out.println(
                    "Purchases loaded from backend: "
                            + purchases.size()
            );

            return purchases;

        } catch (Exception e) {

            System.out.println(
                    "Error loading purchases from backend"
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    // =====================================================
    // Get purchase by ID
    // GET /api/purchases/{id}
    // =====================================================

    public Purchase getPurchaseById(int id) {

        try {

            String json =
                    ApiClient.get(
                            "/purchases/" + id
                    );

            return gson.fromJson(
                    json,
                    Purchase.class
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading purchase from backend"
            );

            e.printStackTrace();

            return null;
        }
    }

    // =====================================================
    // Create purchase
    // POST /api/purchases
    //
    // Backend expects:
    //
    // {
    //     "supplierId": 1,
    //     "items": [
    //         {
    //             "ingredientId": 3,
    //             "quantity": 10,
    //             "cost": 5000
    //         }
    //     ]
    // }
    // =====================================================

    public Purchase createPurchase(
            int supplierId,
            List<PurchaseItemRequest> items
    ) {

        try {

            PurchaseRequest request =
                    new PurchaseRequest(
                            supplierId,
                            items
                    );

            String json =
                    gson.toJson(request);

            String response =
                    ApiClient.post(
                            "/purchases",
                            json
                    );

            System.out.println(
                    "Purchase created through backend: "
                            + response
            );

            return gson.fromJson(
                    response,
                    Purchase.class
            );

        } catch (Exception e) {

            System.out.println(
                    "Error creating purchase through backend"
            );

            e.printStackTrace();

            return null;
        }
    }

    // =====================================================
    // Request object sent to Spring Boot
    // =====================================================

    private static class PurchaseRequest {

        private final int supplierId;

        private final List<PurchaseItemRequest> items;

        private PurchaseRequest(
                int supplierId,
                List<PurchaseItemRequest> items
        ) {

            this.supplierId = supplierId;
            this.items = items;
        }
    }

    // =====================================================
    // Individual purchase item sent to Spring Boot
    // =====================================================

    public static class PurchaseItemRequest {

        private final int ingredientId;

        private final double quantity;

        private final double cost;

        public PurchaseItemRequest(
                int ingredientId,
                double quantity,
                double cost
        ) {

            this.ingredientId = ingredientId;
            this.quantity = quantity;
            this.cost = cost;
        }
    }
}
