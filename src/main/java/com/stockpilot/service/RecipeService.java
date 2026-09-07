package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.database.Database;
import com.stockpilot.model.Recipe;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecipeService {

    private final Gson gson = new Gson();

    // =====================================================
    // Get all recipes
    // Online first -> SQLite fallback
    // =====================================================

    public List<Recipe> getAllRecipes() {

        try {

            String json = ApiClient.get("/recipes");

            Type listType =
                    new TypeToken<List<Recipe>>() {}.getType();

            List<Recipe> recipes =
                    gson.fromJson(json, listType);

            if (recipes == null) {
                recipes = new ArrayList<>();
            }

            cacheRecipes(recipes);

            System.out.println(
                    "Recipes loaded from backend: "
                            + recipes.size()
            );

            return recipes;

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Loading recipes from SQLite."
            );

            return getRecipesFromSQLite();
        }
    }


    // =====================================================
    // Add recipe
    // Online first -> SQLite fallback
    // =====================================================

    public void addRecipe(Recipe recipe) {

        try {

            String json = gson.toJson(recipe);

            String response =
                    ApiClient.post(
                            "/recipes",
                            json
                    );

            System.out.println(
                    "Recipe created through backend: "
                            + response
            );

            // Try to save the backend-created recipe locally.
            try {

                Recipe savedRecipe =
                        gson.fromJson(
                                response,
                                Recipe.class
                        );

                if (savedRecipe != null) {
                    saveRecipeToSQLite(savedRecipe);
                } else {
                    saveRecipeToSQLite(recipe);
                }

            } catch (Exception cacheError) {

                saveRecipeToSQLite(recipe);
            }

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Saving recipe locally."
            );

            saveRecipeToSQLite(recipe);
        }
    }


    // =====================================================
    // Get recipe by ID
    // Online first -> SQLite fallback
    // =====================================================

    public Recipe getRecipeById(int id) {

        try {

            String json =
                    ApiClient.get(
                            "/recipes/" + id
                    );

            Recipe recipe =
                    gson.fromJson(
                            json,
                            Recipe.class
                    );

            if (recipe != null) {
                saveRecipeToSQLite(recipe);
            }

            return recipe;

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Loading recipe from SQLite."
            );

            return getRecipeFromSQLite(id);
        }
    }


    // =====================================================
    // Delete recipe
    // Delete locally regardless of backend availability
    // =====================================================

    public void deleteRecipe(int id) {

        try {

            ApiClient.delete(
                    "/recipes/" + id
            );

            System.out.println(
                    "Recipe deleted through backend."
            );

        } catch (Exception e) {

            System.out.println(
                    "Backend unavailable. Deleting recipe locally."
            );
        }

        deleteRecipeFromSQLite(id);
    }


    // =====================================================
    // SQLite: Save recipe
    // =====================================================

    private void saveRecipeToSQLite(Recipe recipe) {

        if (recipe == null) {
            return;
        }

        String sql =
                """
                INSERT INTO recipes
                (
                    id,
                    name,
                    description,
                    selling_price
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT(id)
                DO UPDATE SET
                    name = excluded.name,
                    description = excluded.description,
                    selling_price = excluded.selling_price
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recipe.getId()
            );

            statement.setString(
                    2,
                    recipe.getName()
            );

            statement.setString(
                    3,
                    recipe.getDescription()
            );

            statement.setDouble(
                    4,
                    recipe.getSellingPrice()
            );

            statement.executeUpdate();

            System.out.println(
                    "Recipe saved locally: "
                            + recipe.getName()
            );

        } catch (Exception e) {

            /*
             * Recipe IDs coming from the backend may not always
             * be suitable for a fresh local record. If the ID is
             * zero, let SQLite generate one.
             */
            if (recipe.getId() == 0) {

                saveRecipeWithoutId(recipe);

            } else {

                System.out.println(
                        "Error saving recipe locally."
                );

                e.printStackTrace();
            }
        }
    }


    // =====================================================
    // SQLite: Save recipe without supplied ID
    // =====================================================

    private void saveRecipeWithoutId(Recipe recipe) {

        String sql =
                """
                INSERT INTO recipes
                (
                    name,
                    description,
                    selling_price
                )
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    recipe.getName()
            );

            statement.setString(
                    2,
                    recipe.getDescription()
            );

            statement.setDouble(
                    3,
                    recipe.getSellingPrice()
            );

            statement.executeUpdate();

            System.out.println(
                    "Recipe saved locally: "
                            + recipe.getName()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error saving recipe locally."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // SQLite: Load all recipes
    // =====================================================

    private List<Recipe> getRecipesFromSQLite() {

        List<Recipe> recipes =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    id,
                    name,
                    description,
                    selling_price
                FROM recipes
                ORDER BY id DESC
                """;

        try (
                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                recipes.add(
                        new Recipe(
                                result.getInt("id"),
                                result.getString("name"),
                                result.getString("description"),
                                result.getDouble("selling_price")
                        )
                );
            }

            System.out.println(
                    "Recipes loaded from SQLite: "
                            + recipes.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipes from SQLite."
            );

            e.printStackTrace();
        }

        return recipes;
    }


    // =====================================================
    // SQLite: Get recipe by ID
    // =====================================================

    private Recipe getRecipeFromSQLite(int id) {

        String sql =
                """
                SELECT
                    id,
                    name,
                    description,
                    selling_price
                FROM recipes
                WHERE id = ?
                """;

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

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (result.next()) {

                    return new Recipe(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getString("description"),
                            result.getDouble("selling_price")
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipe from SQLite."
            );

            e.printStackTrace();
        }

        return null;
    }


    // =====================================================
    // SQLite: Delete recipe
    // =====================================================

    private void deleteRecipeFromSQLite(int id) {

        try (
                Connection connection =
                        Database.connect()
        ) {

            /*
             * Remove recipe ingredients first so the recipe
             * can safely be deleted.
             */
            try (
                    PreparedStatement ingredientsStatement =
                            connection.prepareStatement(
                                    "DELETE FROM recipe_ingredients WHERE recipe_id = ?"
                            )
            ) {

                ingredientsStatement.setInt(
                        1,
                        id
                );

                ingredientsStatement.executeUpdate();
            }


            try (
                    PreparedStatement recipeStatement =
                            connection.prepareStatement(
                                    "DELETE FROM recipes WHERE id = ?"
                            )
            ) {

                recipeStatement.setInt(
                        1,
                        id
                );

                recipeStatement.executeUpdate();
            }

            System.out.println(
                    "Recipe deleted locally: "
                            + id
            );

        } catch (Exception e) {

            System.out.println(
                    "Error deleting recipe locally."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // SQLite: Cache backend recipes
    // =====================================================

    private void cacheRecipes(List<Recipe> recipes) {

        if (recipes == null) {
            return;
        }

        for (Recipe recipe : recipes) {

            try {

                saveRecipeToSQLite(recipe);

            } catch (Exception e) {

                System.out.println(
                        "Could not cache recipe: "
                                + recipe.getName()
                );
            }
        }
    }
}