package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stockpilot.api.ApiClient;
import com.stockpilot.database.Database;
import com.stockpilot.model.RecipeIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientService {

    private final Gson gson = new Gson();

    // =====================================================
    // Add ingredient to recipe
    // POST /api/recipe-ingredients
    // =====================================================

    public void addIngredientToRecipe(
            int recipeId,
            int ingredientId,
            double quantity
    ) {

        try {

            String endpoint =
                    "/recipe-ingredients"
                    + "?recipeId=" + recipeId
                    + "&ingredientId=" + ingredientId
                    + "&quantityUsed=" + quantity;

            String response =
                    ApiClient.post(
                            endpoint,
                            "{}"
                    );

            System.out.println(
                    "Ingredient added through backend: "
                            + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Error adding ingredient through backend."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // Get ingredients for recipe
    // GET /api/recipe-ingredients/recipe/{recipeId}
    // =====================================================

    public List<RecipeIngredient> getRecipeIngredients(
            int recipeId
    ) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        try {

            String json =
                    ApiClient.get(
                            "/recipe-ingredients/recipe/"
                                    + recipeId
                    );

            JsonArray array =
                    JsonParser.parseString(json)
                            .getAsJsonArray();

            for (JsonElement element : array) {

                JsonObject item =
                        element.getAsJsonObject();

                int id =
                        item.get("id")
                                .getAsInt();

                double quantity =
                        item.get("quantityUsed")
                                .getAsDouble();

                JsonObject recipe =
                        item.getAsJsonObject("recipe");

                JsonObject ingredient =
                        item.getAsJsonObject("ingredient");

                int recipeIdFromApi =
                        recipe.get("id")
                                .getAsInt();

                int ingredientId =
                        ingredient.get("id")
                                .getAsInt();

                String ingredientName =
                        ingredient.get("name")
                                .getAsString();

                String unit =
                        ingredient.get("unit")
                                .getAsString();

                ingredients.add(
                        new RecipeIngredient(
                                id,
                                recipeIdFromApi,
                                ingredientId,
                                ingredientName,
                                quantity,
                                unit
                        )
                );
            }

            System.out.println(
                    "Recipe ingredients loaded from backend: "
                            + ingredients.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipe ingredients from backend."
            );

            e.printStackTrace();
        }

        return ingredients;
    }


    // =====================================================
    // Remove ingredient from recipe
    // DELETE /api/recipe-ingredients/{id}
    // =====================================================

    public void removeIngredientFromRecipe(
            int id
    ) {

        try {

            ApiClient.delete(
                    "/recipe-ingredients/" + id
            );

            System.out.println(
                    "Ingredient removed through backend."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error removing ingredient through backend."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // TEMPORARY STOCK DEDUCTION
    //
    // Still uses SQLite because the backend does not yet
    // expose the stock-deduction endpoint.
    // =====================================================

    public void reduceIngredientsForSale(
            int recipeId,
            int soldQuantity
    ) {

        String sql =
                """
                SELECT
                    ingredient_id,
                    quantity_used
                FROM recipe_ingredients
                WHERE recipe_id = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recipeId
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                int ingredientId =
                        result.getInt(
                                "ingredient_id"
                        );

                double amountUsed =
                        result.getDouble(
                                "quantity_used"
                        );

                double totalReduction =
                        amountUsed * soldQuantity;

                updateIngredientStock(
                        ingredientId,
                        totalReduction
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error reducing ingredients for sale."
            );

            e.printStackTrace();
        }
    }


    private void updateIngredientStock(
            int ingredientId,
            double amount
    ) {

        String sql =
                """
                UPDATE ingredients
                SET quantity = quantity - ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDouble(
                    1,
                    amount
            );

            statement.setInt(
                    2,
                    ingredientId
            );

            statement.executeUpdate();

        } catch (Exception e) {

            System.out.println(
                    "Error updating ingredient stock."
            );

            e.printStackTrace();
        }
    }


    public List<RecipeIngredient> getIngredientsForRecipe(
            int recipeId
    ) {

        return getRecipeIngredients(recipeId);
    }
}