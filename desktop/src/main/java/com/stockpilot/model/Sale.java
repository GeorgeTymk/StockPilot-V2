package com.stockpilot.model;

public class Sale {

    private int id;

    private int recipeId;

    private String recipeName;

    private int quantity;

    private double total;

    private String saleDate;

    public Sale(
            int id,
            int recipeId,
            String recipeName,
            int quantity,
            double total,
            String saleDate
    ) {

        this.id = id;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.quantity = quantity;
        this.total = total;
        this.saleDate = saleDate;

    }

    public int getId() {

        return id;

    }

    public int getRecipeId() {

        return recipeId;

    }

    public String getRecipeName() {

        return recipeName;

    }

    public int getQuantity() {

        return quantity;

    }

    public double getTotal() {

        return total;

    }

    public String getSaleDate() {

        return saleDate;

    }

}