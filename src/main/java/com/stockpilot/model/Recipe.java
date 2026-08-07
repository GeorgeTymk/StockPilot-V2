package com.stockpilot.model;


public class Recipe {


    private int id;

    private String name;

    private String description;

    private double sellingPrice;



    public Recipe(
            int id,
            String name,
            String description,
            double sellingPrice
    ){

        this.id = id;
        this.name = name;
        this.description = description;
        this.sellingPrice = sellingPrice;

    }



    public int getId(){

        return id;

    }



    public String getName(){

        return name;

    }



    public String getDescription(){

        return description;

    }



    public double getSellingPrice(){

        return sellingPrice;

    }



    public void setName(String name){

        this.name = name;

    }



    public void setDescription(String description){

        this.description = description;

    }



    public void setSellingPrice(double sellingPrice){

        this.sellingPrice = sellingPrice;

    }



    @Override
    public String toString(){

        return name;

    }


}