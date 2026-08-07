package com.stockpilot.model;

public class RecipeIngredient {

    private int id;

    private int recipeId;

    private int ingredientId;

    private String ingredientName;

    private double quantity;

    private String unit;

    public RecipeIngredient(
            int id,
            int recipeId,
            int ingredientId,
            String ingredientName,
            double quantity,
            String unit
    ) {

        this.id = id;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;

    }

    public int getId() {

        return id;

    }

    public int getRecipeId() {

        return recipeId;

    }

    public int getIngredientId() {

        return ingredientId;

    }

    public String getIngredientName() {

        return ingredientName;

    }

    public double getQuantity() {

        return quantity;

    }

    public String getUnit() {

        return unit;

    }

    @Override
    public String toString() {

        return ingredientName + " (" + quantity + " " + unit + ")";

    }

}