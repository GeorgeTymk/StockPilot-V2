package com.stockpilot.model;


public class PurchaseItem {


    private int id;

    private int purchaseId;

    private int ingredientId;

    private double quantity;

    private double cost;



    public PurchaseItem(
            int id,
            int purchaseId,
            int ingredientId,
            double quantity,
            double cost
    ){

        this.id = id;
        this.purchaseId = purchaseId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.cost = cost;

    }



    public int getId(){

        return id;

    }



    public int getPurchaseId(){

        return purchaseId;

    }



    public int getIngredientId(){

        return ingredientId;

    }



    public double getQuantity(){

        return quantity;

    }



    public double getCost(){

        return cost;

    }

}