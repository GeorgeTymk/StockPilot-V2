package com.stockpilot.controller;


import com.stockpilot.service.IngredientService;
import com.stockpilot.util.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class AddIngredientController {


    @FXML
    private TextField nameField;


    @FXML
    private TextField quantityField;


    @FXML
    private TextField unitField;


    @FXML
    private TextField minimumStockField;



    private final IngredientService ingredientService =
            new IngredientService();




    @FXML
    private void goBack(){

        Navigator.goBack();

    }





    @FXML
    public void saveIngredient(){


        String name =
                nameField.getText();


        String quantityText =
                quantityField.getText();


        String unit =
                unitField.getText();


        String minimumStockText =
                minimumStockField.getText();




        if(name.isBlank()
                || quantityText.isBlank()
                || unit.isBlank()
                || minimumStockText.isBlank()){


            System.out.println(
                    "Please fill all fields"
            );

            return;

        }





        try{


            double quantity =
                    Double.parseDouble(quantityText);



            double minimumStock =
                    Double.parseDouble(minimumStockText);




            ingredientService.addIngredient(

                    name,

                    quantity,

                    unit,

                    minimumStock

            );



            System.out.println(
                    "Ingredient saved successfully"
            );



            nameField.clear();

            quantityField.clear();

            unitField.clear();

            minimumStockField.clear();



        }
        catch(NumberFormatException e){


            System.out.println(
                    "Quantity and minimum stock must be numbers"
            );


        }
        catch(Exception e){

            e.printStackTrace();

        }


    }


}