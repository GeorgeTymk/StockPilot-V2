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
    // Online first -> SQLite fallback
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

            /*
             * Save the relationship locally too.
             */
            saveRecipeIngredientToSQLite(
                    recipeId,
                    ingredientId,
                    quantity
            );

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Saving recipe ingredient locally."
            );

            saveRecipeIngredientToSQLite(
                    recipeId,
                    ingredientId,
                    quantity
            );
        }
    }


    // =====================================================
    // Get ingredients for recipe
    // Online first -> SQLite fallback
    // =====================================================

    public List<RecipeIngredient> getRecipeIngredients(
            int recipeId
    ) {

        try {

            String json =
                    ApiClient.get(
                            "/recipe-ingredients/recipe/"
                                    + recipeId
                    );

            List<RecipeIngredient> ingredients =
                    parseBackendIngredients(json);

            /*
             * Cache backend data locally.
             */
            cacheRecipeIngredients(
                    ingredients
            );

            System.out.println(
                    "Recipe ingredients loaded from backend: "
                            + ingredients.size()
            );

            return ingredients;

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Loading recipe ingredients from SQLite."
            );

            return getRecipeIngredientsFromSQLite(
                    recipeId
            );
        }
    }


    // =====================================================
    // Parse backend recipe ingredients
    // =====================================================

    private List<RecipeIngredient> parseBackendIngredients(
            String json
    ) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

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

            int recipeId =
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
                            recipeId,
                            ingredientId,
                            ingredientName,
                            quantity,
                            unit
                    )
            );
        }

        return ingredients;
    }


    // =====================================================
    // Remove ingredient from recipe
    // Online first -> SQLite always
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
                    "Backend unavailable. Removing recipe ingredient locally."
            );
        }

        deleteRecipeIngredientFromSQLite(id);
    }


    // =====================================================
    // Save recipe ingredient locally
    // =====================================================

    private void saveRecipeIngredientToSQLite(
            int recipeId,
            int ingredientId,
            double quantity
    ) {

        String sql =
                """
                INSERT INTO recipe_ingredients
                (
                    recipe_id,
                    ingredient_id,
                    quantity_used
                )
                VALUES (?, ?, ?)
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

            statement.setInt(
                    2,
                    ingredientId
            );

            statement.setDouble(
                    3,
                    quantity
            );

            statement.executeUpdate();

            System.out.println(
                    "Recipe ingredient saved locally."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error saving recipe ingredient locally."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // Load recipe ingredients from SQLite
    // =====================================================

    private List<RecipeIngredient>
    getRecipeIngredientsFromSQLite(
            int recipeId
    ) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    ri.id,
                    ri.recipe_id,
                    ri.ingredient_id,
                    ri.quantity_used,
                    i.name,
                    i.unit
                FROM recipe_ingredients ri
                INNER JOIN ingredients i
                    ON i.id = ri.ingredient_id
                WHERE ri.recipe_id = ?
                ORDER BY ri.id
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

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                while (result.next()) {

                    ingredients.add(
                            new RecipeIngredient(
                                    result.getInt("id"),
                                    result.getInt("recipe_id"),
                                    result.getInt("ingredient_id"),
                                    result.getString("name"),
                                    result.getDouble("quantity_used"),
                                    result.getString("unit")
                            )
                    );
                }
            }

            System.out.println(
                    "Recipe ingredients loaded from SQLite: "
                            + ingredients.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipe ingredients from SQLite."
            );

            e.printStackTrace();
        }

        return ingredients;
    }


    // =====================================================
    // Cache backend recipe ingredients
    // =====================================================

    private void cacheRecipeIngredients(
            List<RecipeIngredient> ingredients
    ) {

        if (ingredients == null) {
            return;
        }

        for (RecipeIngredient ingredient : ingredients) {

            String sql =
                    """
                    INSERT INTO recipe_ingredients
                    (
                        id,
                        recipe_id,
                        ingredient_id,
                        quantity_used
                    )
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT(id)
                    DO UPDATE SET
                        recipe_id = excluded.recipe_id,
                        ingredient_id = excluded.ingredient_id,
                        quantity_used = excluded.quantity_used
                    """;

            try (
                    Connection connection =
                            Database.connect();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(
                        1,
                        ingredient.getId()
                );

                statement.setInt(
                        2,
                        ingredient.getRecipeId()
                );

                statement.setInt(
                        3,
                        ingredient.getIngredientId()
                );

                statement.setDouble(
                        4,
                        ingredient.getQuantity()
                );

                statement.executeUpdate();

            } catch (Exception e) {

                System.out.println(
                        "Could not cache recipe ingredient."
                );
            }
        }
    }


    // =====================================================
    // Delete recipe ingredient locally
    // =====================================================

    private void deleteRecipeIngredientFromSQLite(
            int id
    ) {

        String sql =
                "DELETE FROM recipe_ingredients WHERE id = ?";

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    id
            );

            statement.executeUpdate();

        } catch (Exception e) {

            System.out.println(
                    "Error deleting recipe ingredient locally."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // STOCK DEDUCTION
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


    // =====================================================
    // Alias used by existing controllers
    // =====================================================

    public List<RecipeIngredient> getIngredientsForRecipe(
            int recipeId
    ) {

        return getRecipeIngredients(recipeId);
    }
}