package com.stockpilot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.model.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeService {

    private final Gson gson = new Gson();

    // =====================================================
    // Get all recipes
    // GET /api/recipes
    // =====================================================

    public List<Recipe> getAllRecipes() {

        try {

            String json =
                    ApiClient.get("/recipes");

            Type listType =
                    new TypeToken<List<Recipe>>() {}.getType();

            List<Recipe> recipes =
                    gson.fromJson(json, listType);

            if (recipes == null) {
                return new ArrayList<>();
            }

            System.out.println(
                    "Recipes loaded from backend: "
                            + recipes.size()
            );

            return recipes;

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipes from backend"
            );

            e.printStackTrace();

            return new ArrayList<>();
        }
    }


    // =====================================================
    // Add recipe
    // POST /api/recipes
    // =====================================================

    public void addRecipe(Recipe recipe) {

        try {

            String json =
                    gson.toJson(recipe);

            String response =
                    ApiClient.post(
                            "/recipes",
                            json
                    );

            System.out.println(
                    "Recipe created through backend: "
                            + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Error adding recipe through backend"
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // Get recipe by ID
    // GET /api/recipes/{id}
    // =====================================================

    public Recipe getRecipeById(int id) {

        try {

            String json =
                    ApiClient.get(
                            "/recipes/" + id
                    );

            return gson.fromJson(
                    json,
                    Recipe.class
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipe"
            );

            e.printStackTrace();

            return null;
        }
    }


    // =====================================================
    // Delete recipe
    // DELETE /api/recipes/{id}
    // =====================================================

    public void deleteRecipe(int id) {

        try {

            ApiClient.delete(
                    "/recipes/" + id
            );

            System.out.println(
                    "Recipe deleted through backend"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error deleting recipe"
            );

            e.printStackTrace();
        }
    }
}