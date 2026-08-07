package com.stockpilot.model;


public class InventoryHistory {


    private int id;

    private int ingredientId;

    private String ingredientName;

    private String movementType;

    private double quantity;

    private String createdAt;



    public InventoryHistory(
            int id,
            int ingredientId,
            String ingredientName,
            String movementType,
            double quantity,
            String createdAt
    ){

        this.id = id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.movementType = movementType;
        this.quantity = quantity;
        this.createdAt = createdAt;

    }



    public int getId(){

        return id;

    }



    public int getIngredientId(){

        return ingredientId;

    }



    public String getIngredientName(){

        return ingredientName;

    }



    public String getMovementType(){

        return movementType;

    }



    public double getQuantity(){

        return quantity;

    }



    public String getCreatedAt(){

        return createdAt;

    }


}