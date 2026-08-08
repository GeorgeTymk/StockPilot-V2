package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.model.Supplier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SupplierService {

    private final Gson gson = new Gson();

    // =====================================================
    // Get all suppliers
    // GET /api/suppliers
    // =====================================================

    public List<Supplier> getAllSuppliers() {

        try {

            String json =
                    ApiClient.get("/suppliers");

            Type listType =
                    new TypeToken<List<Supplier>>() {}.getType();

            List<Supplier> suppliers =
                    gson.fromJson(json, listType);

            if (suppliers == null) {
                return new ArrayList<>();
            }

            System.out.println(
                    "Suppliers loaded from backend: "
                            + suppliers.size()
            );

            return suppliers;

        } catch (Exception e) {

            System.out.println(
                    "Error loading suppliers from backend"
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    // =====================================================
    // Add supplier
    // POST /api/suppliers
    // =====================================================

    public void addSupplier(Supplier supplier) {

        try {

            String json =
                    gson.toJson(supplier);

            String response =
                    ApiClient.post(
                            "/suppliers",
                            json
                    );

            System.out.println(
                    "Supplier created through backend: "
                            + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Error adding supplier through backend"
            );

            e.printStackTrace();
        }
    }
}
