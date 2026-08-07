package com.stockpilot.controller;

import com.stockpilot.model.Recipe;
import com.stockpilot.service.RecipeService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.List;

public class SellRecipeController {

    @FXML
    private ComboBox<Recipe> recipeComboBox;

    @FXML
    private TextField quantityField;

    private final RecipeService recipeService =
            new RecipeService();

    @FXML
    public void initialize() {

        loadRecipes();

    }

    private void loadRecipes() {

        List<Recipe> recipes =
                recipeService.getAllRecipes();

        recipeComboBox.setItems(
                FXCollections.observableArrayList(recipes)
        );

        System.out.println(
                "Recipes loaded: "
                        + recipes.size()
        );

    }

    @FXML
    private void sellRecipe() {

        Recipe recipe =
                recipeComboBox.getValue();

        if (recipe == null) {

            System.out.println(
                    "Please select a recipe."
            );

            return;

        }

        if (quantityField.getText().isBlank()) {

            System.out.println(
                    "Please enter quantity."
            );

            return;

        }

        try {

            int quantity =
                    Integer.parseInt(
                            quantityField.getText()
                    );

            if (quantity <= 0) {

                System.out.println(
                        "Quantity must be greater than zero."
                );

                return;

            }

            System.out.println(
                    "Recipe: "
                            + recipe.getName()
            );

            System.out.println(
                    "Quantity: "
                            + quantity
            );

            // Inventory deduction comes next.

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid quantity."
            );

        }

    }

    @FXML
    private void goBack() {

        Navigator.goBack();

    }

}