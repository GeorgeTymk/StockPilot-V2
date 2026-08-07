package com.stockpilot.controller;


import com.stockpilot.model.Supplier;
import com.stockpilot.service.SupplierService;
import com.stockpilot.util.Navigator;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;



public class AddSupplierController {



    @FXML
    private TextField nameField;

    @FXML
private void goBack(){

    Navigator.goBack();

}


    @FXML
    private TextField phoneField;



    @FXML
    private TextField emailField;




    private final SupplierService supplierService =
            new SupplierService();





    @FXML
    private void saveSupplier(){


        try {



            Supplier supplier =
                    new Supplier(

                            0,

                            nameField.getText(),

                            phoneField.getText(),

                            emailField.getText()

                    );



            supplierService.addSupplier(supplier);




            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);


            alert.setTitle("Success");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Supplier added successfully"
            );


            alert.showAndWait();



            Navigator.goTo("suppliers.fxml");




        } catch(Exception e){



            Alert alert =
                    new Alert(Alert.AlertType.ERROR);


            alert.setTitle("Error");

            alert.setHeaderText(null);

            alert.setContentText(
                    "Could not save supplier"
            );


            alert.showAndWait();


        }


    }


}