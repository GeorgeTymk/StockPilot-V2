package com.stockpilot.controller;


import com.stockpilot.model.Supplier;
import com.stockpilot.model.Ingredient;

import com.stockpilot.service.SupplierService;
import com.stockpilot.service.IngredientService;
import com.stockpilot.service.RestockService;

import com.stockpilot.util.Navigator;


import javafx.collections.FXCollections;

import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;



public class RestockController {



    @FXML
    private ComboBox<Supplier> supplierComboBox;


    @FXML
    private ComboBox<Ingredient> ingredientComboBox;


    @FXML
    private TextField quantityField;


    @FXML
    private TextField costField;



    private final SupplierService supplierService =
            new SupplierService();



    private final IngredientService ingredientService =
            new IngredientService();



    private final RestockService restockService =
            new RestockService();





    @FXML
    public void initialize(){


        supplierComboBox.setItems(

                FXCollections.observableArrayList(

                        supplierService.getAllSuppliers()

                )

        );



        ingredientComboBox.setItems(

                FXCollections.observableArrayList(

                        ingredientService.getAllIngredients()

                )

        );


    }







    @FXML
    private void saveRestock(){



        Supplier supplier =
                supplierComboBox.getValue();



        Ingredient ingredient =
                ingredientComboBox.getValue();




        if(supplier == null || ingredient == null){

            System.out.println(
                    "Select supplier and ingredient"
            );

            return;

        }





        try{


            double quantity =
                    Double.parseDouble(
                            quantityField.getText()
                    );



            double cost =
                    Double.parseDouble(
                            costField.getText()
                    );





            boolean success =
                    restockService.restock(

                            supplier.getId(),

                            ingredient.getId(),

                            quantity,

                            cost

                    );




            if(success){


                System.out.println(
                        "Stock added successfully"
                );


                quantityField.clear();

                costField.clear();


            }




        }
        catch(Exception e){

            e.printStackTrace();

        }


    }







    @FXML
    private void goBack(){

        Navigator.goBack();

    }


}