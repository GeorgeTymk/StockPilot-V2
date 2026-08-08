package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.model.Ingredient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IngredientService {

    private final Gson gson = new Gson();

    // =====================================================
    // API RESPONSE WRAPPER
    // =====================================================

    private static class IngredientResponse {

        private List<Ingredient> value;

        private int Count;
    }

    // =====================================================
    // GET ALL INGREDIENTS
    // GET /api/ingredients
    // =====================================================

    public List<Ingredient> getAllIngredients() {

    try {

        String json =
                ApiClient.get("/ingredients");

        Type listType =
                new TypeToken<List<Ingredient>>() {}.getType();

        List<Ingredient> ingredients =
                gson.fromJson(json, listType);

        if (ingredients == null) {

            return new ArrayList<>();

        }

        System.out.println(
                "Ingredients loaded from backend: "
                        + ingredients.size()
        );

        return ingredients;

    }
    catch (Exception e) {

        System.out.println(
                "Error loading ingredients from backend"
        );

        e.printStackTrace();

        return new ArrayList<>();
    }
}

    // =====================================================
    // GET INGREDIENT BY ID
    // GET /api/ingredients/{id}
    // =====================================================

    public Ingredient getIngredientById(int id) {

        try {

            String json =
                    ApiClient.get(
                            "/ingredients/" + id
                    );

            return gson.fromJson(
                    json,
                    Ingredient.class
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Error loading ingredient from backend"
            );

            e.printStackTrace();

            return null;
        }
    }

    // =====================================================
    // GET LOW STOCK INGREDIENTS
    // =====================================================

    public List<Ingredient> getLowStockIngredients() {

        List<Ingredient> ingredients =
                getAllIngredients();

        List<Ingredient> lowStock =
                new ArrayList<>();

        for (Ingredient ingredient : ingredients) {

            if (ingredient.getQuantity() > 0
                    && ingredient.getQuantity()
                    <= ingredient.getMinimumStock()) {

                lowStock.add(ingredient);
            }
        }

        return lowStock;
    }

    // =====================================================
    // GET OUT OF STOCK INGREDIENTS
    // =====================================================

    public List<Ingredient> getOutOfStockIngredients() {

        List<Ingredient> ingredients =
                getAllIngredients();

        List<Ingredient> outOfStock =
                new ArrayList<>();

        for (Ingredient ingredient : ingredients) {

            if (ingredient.getQuantity() <= 0) {

                outOfStock.add(ingredient);
            }
        }

        return outOfStock;
    }

    // =====================================================
    // GET STOCK ALERTS
    // LOW STOCK + OUT OF STOCK
    // =====================================================

    public List<Ingredient> getStockAlerts() {

        List<Ingredient> ingredients =
                getAllIngredients();

        List<Ingredient> alerts =
                new ArrayList<>();

        for (Ingredient ingredient : ingredients) {

            if (ingredient.getQuantity()
                    <= ingredient.getMinimumStock()) {

                alerts.add(ingredient);
            }
        }

        return alerts;
    }

    // =====================================================
    // COUNTS
    // =====================================================

    public int getIngredientCount() {

        return getAllIngredients().size();
    }

    public int getLowStockCount() {

        return getLowStockIngredients().size();
    }

    public int getOutOfStockCount() {

        return getOutOfStockIngredients().size();
    }

    // =====================================================
    // ADD INGREDIENT
    // POST /api/ingredients
    // =====================================================

    public Ingredient addIngredient(
            String name,
            double quantity,
            String unit,
            double minimumStock
    ) {

        try {

            IngredientRequest request =
                    new IngredientRequest(
                            name,
                            quantity,
                            unit,
                            minimumStock
                    );

            String json =
                    gson.toJson(request);

            String response =
                    ApiClient.post(
                            "/ingredients",
                            json
                    );

            System.out.println(
                    "Ingredient created through backend"
            );

            return gson.fromJson(
                    response,
                    Ingredient.class
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Error creating ingredient through backend"
            );

            e.printStackTrace();

            return null;
        }
    }

    // =====================================================
    // ADD STOCK
    // PUT /api/ingredients/{id}
    // =====================================================

    public void addStock(
            int ingredientId,
            double quantity
    ) {

        try {

            Ingredient ingredient =
                    getIngredientById(ingredientId);

            if (ingredient == null) {

                System.out.println(
                        "Ingredient not found"
                );

                return;
            }

            double newQuantity =
                    ingredient.getQuantity()
                            + quantity;

            ingredient.setQuantity(
                    newQuantity
            );

            String json =
                    gson.toJson(ingredient);

            ApiClient.put(
                    "/ingredients/" + ingredientId,
                    json
            );

            System.out.println(
                    "Stock updated through backend"
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Error updating ingredient stock"
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // REQUEST OBJECT
    // =====================================================

    private static class IngredientRequest {

        private final String name;

        private final double quantity;

        private final String unit;

        private final double minimumStock;

        private IngredientRequest(
                String name,
                double quantity,
                String unit,
                double minimumStock
        ) {

            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.minimumStock = minimumStock;
        }
    }
}