package com.stockpilot.controller;


import com.stockpilot.model.Product;
import com.stockpilot.service.InventoryService;
import com.stockpilot.util.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class AddProductController {


    @FXML
private void goBack(){

    Navigator.goBack();

}

    @FXML
    private TextField nameField;


    @FXML
    private TextField categoryField;


    @FXML
    private TextField quantityField;


    @FXML
    private TextField priceField;



    private final InventoryService inventoryService =
            new InventoryService();



    @FXML
    private void saveProduct() {


        try {


            String name = nameField.getText();

            String category = categoryField.getText();

            int quantity = Integer.parseInt(quantityField.getText());

            double price = Double.parseDouble(priceField.getText());



            Product product = new Product(

                    0,

                    name,

                    category,

                    quantity,

                    price

            );



            inventoryService.addProduct(product);



            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Product Added");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Product added successfully"
            );

            alert.showAndWait();



            Navigator.goTo("inventory.fxml");



        } catch (NumberFormatException e) {


            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Invalid Input");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Quantity and price must be numbers"
            );

            alert.showAndWait();



        } catch (Exception e) {


            e.printStackTrace();


            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Error");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Something went wrong while saving the product"
            );

            alert.showAndWait();


        }


    }


}