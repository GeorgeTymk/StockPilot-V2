package com.stockpilot.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stockpilot.api.ApiClient;
import com.stockpilot.model.Ingredient;
import com.stockpilot.service.RecipeIngredientService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeIngredientController {

    @FXML
    private ComboBox<Ingredient> ingredientComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private Label unitLabel;

    private final RecipeIngredientService service =
            new RecipeIngredientService();

    private final Gson gson =
            new Gson();

    private final List<Ingredient> ingredients =
            new ArrayList<>();


    @FXML
    public void initialize() {

        loadIngredients();

        ingredientComboBox.setOnAction(event -> {

            Ingredient ingredient =
                    ingredientComboBox.getValue();

            if (ingredient != null) {

                unitLabel.setText(
                        "Unit: " + ingredient.getUnit()
                );
            }
        });
    }


    // =====================================================
    // Load ingredients from backend
    // GET /api/ingredients
    // =====================================================

    private void loadIngredients() {

        try {

            String json =
                    ApiClient.get("/ingredients");

            Type listType =
                    new TypeToken<List<Ingredient>>() {}
                            .getType();

            List<Ingredient> loadedIngredients =
                    gson.fromJson(
                            json,
                            listType
                    );

            ingredients.clear();

            if (loadedIngredients != null) {

                ingredients.addAll(
                        loadedIngredients
                );
            }

            ingredientComboBox.setItems(
                    FXCollections.observableArrayList(
                            ingredients
                    )
            );

            System.out.println(
                    "Ingredients loaded from backend: "
                            + ingredients.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading ingredients from backend."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // Save ingredient to selected recipe
    // =====================================================

    @FXML
    private void saveIngredient() {

        Ingredient ingredient =
                ingredientComboBox.getValue();

        if (ingredient == null) {

            System.out.println(
                    "Please select an ingredient"
            );

            return;
        }

        if (quantityField.getText().isBlank()) {

            System.out.println(
                    "Enter quantity"
            );

            return;
        }

        try {

            double quantity =
                    Double.parseDouble(
                            quantityField.getText()
                    );

            if (RecipeController.selectedRecipe == null) {

                System.out.println(
                        "No recipe selected."
                );

                return;
            }

            service.addIngredientToRecipe(

                    RecipeController
                            .selectedRecipe
                            .getId(),

                    ingredient.getId(),

                    quantity
            );

            System.out.println(
                    "Ingredient added successfully"
            );

            Navigator.goBack();

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid quantity"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error adding ingredient."
            );

            e.printStackTrace();
        }
    }


    @FXML
    private void goBack() {

        Navigator.goBack();
    }
}