package com.stockpilot.model;


public class Purchase {


    private int id;

    private int supplierId;

    private String supplierName;

    private double totalCost;

    private String purchaseDate;



    public Purchase(
            int id,
            int supplierId,
            String supplierName,
            double totalCost,
            String purchaseDate
    ){

        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.totalCost = totalCost;
        this.purchaseDate = purchaseDate;

    }



    public int getId(){

        return id;

    }



    public int getSupplierId(){

        return supplierId;

    }



    public String getSupplierName(){

        return supplierName;

    }



    public double getTotalCost(){

        return totalCost;

    }



    public String getPurchaseDate(){

        return purchaseDate;

    }

}