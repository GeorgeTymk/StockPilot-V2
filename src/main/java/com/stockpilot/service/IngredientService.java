package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.database.Database;
import com.stockpilot.model.Ingredient;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    // API FIRST -> SQLITE FALLBACK
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

            // Keep SQLite cache updated when API is available.
            cacheIngredients(ingredients);

            System.out.println(
                    "Ingredients loaded from backend: "
                            + ingredients.size()
            );

            return ingredients;

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Loading ingredients from SQLite."
            );

            return getLocalIngredients();
        }
    }

    // =====================================================
    // GET INGREDIENT BY ID
    // API FIRST -> SQLITE FALLBACK
    // =====================================================

    public Ingredient getIngredientById(int id) {

        try {

            String json =
                    ApiClient.get(
                            "/ingredients/" + id
                    );

            Ingredient ingredient =
                    gson.fromJson(
                            json,
                            Ingredient.class
                    );

            if (ingredient != null) {
                cacheIngredient(ingredient);
            }

            return ingredient;

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Loading ingredient locally."
            );

            return getLocalIngredient(id);
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
    // API FIRST -> SQLITE FALLBACK
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

            Ingredient saved =
                    gson.fromJson(
                            response,
                            Ingredient.class
                    );

            if (saved != null) {

                cacheIngredient(saved);

                System.out.println(
                        "Ingredient created through backend"
                );

                return saved;
            }

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Saving ingredient locally."
            );
        }

        return addLocalIngredient(
                name,
                quantity,
                unit,
                minimumStock
        );
    }

    // =====================================================
    // ADD STOCK
    // API FIRST -> SQLITE FALLBACK
    // =====================================================

    public void addStock(
            int ingredientId,
            double quantity
    ) {

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

        ingredient.setQuantity(newQuantity);

        try {

            String json =
                    gson.toJson(ingredient);

            ApiClient.put(
                    "/ingredients/" + ingredientId,
                    json
            );

            cacheIngredient(ingredient);

            System.out.println(
                    "Stock updated through backend"
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Updating stock locally."
            );

            updateLocalIngredient(ingredient);
        }
    }

    // =====================================================
    // UPDATE INGREDIENT
    // API FIRST -> SQLITE FALLBACK
    // =====================================================

    public Ingredient updateIngredient(
            Ingredient ingredient
    ) {

        try {

            String json =
                    gson.toJson(ingredient);

            String response =
                    ApiClient.put(
                            "/ingredients/"
                                    + ingredient.getId(),
                            json
                    );

            Ingredient updated =
                    gson.fromJson(
                            response,
                            Ingredient.class
                    );

            if (updated != null) {

                cacheIngredient(updated);

                System.out.println(
                        "Ingredient updated through backend"
                );

                return updated;
            }

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Updating ingredient locally."
            );
        }

        updateLocalIngredient(ingredient);

        return ingredient;
    }

    // =====================================================
    // DELETE INGREDIENT
    // API + SQLITE
    // =====================================================

    public void deleteIngredient(
            int ingredientId
    ) {

        try {

            ApiClient.delete(
                    "/ingredients/" + ingredientId
            );

            System.out.println(
                    "Ingredient deleted through backend"
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Deleting ingredient locally."
            );
        }

        deleteLocalIngredient(ingredientId);
    }

    // =====================================================
    // SQLITE - GET ALL
    // =====================================================

    private List<Ingredient> getLocalIngredients() {

        List<Ingredient> ingredients =
                new ArrayList<>();

        String sql =
                "SELECT id, name, quantity, unit, minimum_stock " +
                "FROM ingredients ORDER BY name";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                ingredients.add(
                        mapIngredient(resultSet)
                );
            }

            System.out.println(
                    "Ingredients loaded from SQLite: "
                            + ingredients.size()
            );

        }
        catch (Exception e) {

            System.out.println(
                    "Error loading ingredients from SQLite"
            );

            e.printStackTrace();
        }

        return ingredients;
    }

    // =====================================================
    // SQLITE - GET ONE
    // =====================================================

    private Ingredient getLocalIngredient(int id) {

        String sql =
                "SELECT id, name, quantity, unit, minimum_stock " +
                "FROM ingredients WHERE id = ?";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return mapIngredient(resultSet);
                }
            }

        }
        catch (Exception e) {

            System.out.println(
                    "Error loading local ingredient"
            );

            e.printStackTrace();
        }

        return null;
    }

    // =====================================================
    // SQLITE - ADD
    // =====================================================

    private Ingredient addLocalIngredient(
            String name,
            double quantity,
            String unit,
            double minimumStock
    ) {

        String sql =
                "INSERT INTO ingredients " +
                "(name, quantity, unit, minimum_stock) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, name);
            statement.setDouble(2, quantity);
            statement.setString(3, unit);
            statement.setDouble(4, minimumStock);

            statement.executeUpdate();

            try (
                    ResultSet keys =
                            statement.getGeneratedKeys()
            ) {

                if (keys.next()) {

                    int id =
                            keys.getInt(1);

                    System.out.println(
                            "Ingredient saved locally: "
                                    + name
                    );

                    return new Ingredient(
                            id,
                            name,
                            quantity,
                            unit,
                            minimumStock
                    );
                }
            }

        }
        catch (Exception e) {

            System.out.println(
                    "Error saving ingredient locally"
            );

            e.printStackTrace();
        }

        return null;
    }

    // =====================================================
    // SQLITE - UPDATE
    // =====================================================

    private void updateLocalIngredient(
            Ingredient ingredient
    ) {

        String sql =
                "UPDATE ingredients SET " +
                "name = ?, " +
                "quantity = ?, " +
                "unit = ?, " +
                "minimum_stock = ? " +
                "WHERE id = ?";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    ingredient.getName()
            );

            statement.setDouble(
                    2,
                    ingredient.getQuantity()
            );

            statement.setString(
                    3,
                    ingredient.getUnit()
            );

            statement.setDouble(
                    4,
                    ingredient.getMinimumStock()
            );

            statement.setInt(
                    5,
                    ingredient.getId()
            );

            statement.executeUpdate();

        }
        catch (Exception e) {

            System.out.println(
                    "Error updating ingredient locally"
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // SQLITE - DELETE
    // =====================================================

    private void deleteLocalIngredient(
            int ingredientId
    ) {

        String sql =
                "DELETE FROM ingredients WHERE id = ?";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    ingredientId
            );

            statement.executeUpdate();

        }
        catch (Exception e) {

            System.out.println(
                    "Error deleting ingredient locally"
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // SQLITE - CACHE ONE
    // =====================================================

    private void cacheIngredient(
            Ingredient ingredient
    ) {

        if (ingredient == null) {
            return;
        }

        String updateSql =
                "UPDATE ingredients SET " +
                "name = ?, " +
                "quantity = ?, " +
                "unit = ?, " +
                "minimum_stock = ? " +
                "WHERE id = ?";

        String insertSql =
                "INSERT INTO ingredients " +
                "(id, name, quantity, unit, minimum_stock) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        Database.connect()
        ) {

            try (
                    PreparedStatement update =
                            connection.prepareStatement(
                                    updateSql
                            )
            ) {

                update.setString(
                        1,
                        ingredient.getName()
                );

                update.setDouble(
                        2,
                        ingredient.getQuantity()
                );

                update.setString(
                        3,
                        ingredient.getUnit()
                );

                update.setDouble(
                        4,
                        ingredient.getMinimumStock()
                );

                update.setInt(
                        5,
                        ingredient.getId()
                );

                int rows =
                        update.executeUpdate();

                if (rows == 0) {

                    try (
                            PreparedStatement insert =
                                    connection.prepareStatement(
                                            insertSql
                                    )
                    ) {

                        insert.setInt(
                                1,
                                ingredient.getId()
                        );

                        insert.setString(
                                2,
                                ingredient.getName()
                        );

                        insert.setDouble(
                                3,
                                ingredient.getQuantity()
                        );

                        insert.setString(
                                4,
                                ingredient.getUnit()
                        );

                        insert.setDouble(
                                5,
                                ingredient.getMinimumStock()
                        );

                        insert.executeUpdate();
                    }
                }
            }

        }
        catch (Exception e) {

            System.out.println(
                    "Could not cache ingredient locally: "
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // SQLITE - CACHE ALL
    // =====================================================

    private void cacheIngredients(
            List<Ingredient> ingredients
    ) {

        if (ingredients == null) {
            return;
        }

        for (Ingredient ingredient : ingredients) {

            cacheIngredient(ingredient);
        }
    }

    // =====================================================
    // DATABASE MAPPING
    // =====================================================

    private Ingredient mapIngredient(
            ResultSet resultSet
    ) throws Exception {

        return new Ingredient(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getDouble("quantity"),
                resultSet.getString("unit"),
                resultSet.getDouble("minimum_stock")
        );
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