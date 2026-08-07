package com.stockpilot.model;


public class Ingredient {


    private int id;

    private String name;

    private double quantity;

    private String unit;

    private double minimumStock;



    public Ingredient(
            int id,
            String name,
            double quantity,
            String unit,
            double minimumStock
    ){

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.minimumStock = minimumStock;

    }



    public int getId(){

        return id;

    }


    public void setId(int id){

        this.id = id;

    }



    public String getName(){

        return name;

    }


    public void setName(String name){

        this.name = name;

    }



    public double getQuantity(){

        return quantity;

    }


    public void setQuantity(double quantity){

        this.quantity = quantity;

    }



    public String getUnit(){

        return unit;

    }


    public void setUnit(String unit){

        this.unit = unit;

    }



    public double getMinimumStock(){

        return minimumStock;

    }


    public void setMinimumStock(double minimumStock){

        this.minimumStock = minimumStock;

    }



    @Override
    public String toString(){

        return name;

    }



}