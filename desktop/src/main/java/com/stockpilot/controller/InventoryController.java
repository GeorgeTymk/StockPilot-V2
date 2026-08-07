package com.stockpilot.controller;

import com.stockpilot.model.Ingredient;
import com.stockpilot.service.IngredientService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;


public class InventoryController {


    @FXML
    private TableView<Ingredient> ingredientTable;


    @FXML
    private TableColumn<Ingredient, String> nameColumn;


    @FXML
    private TableColumn<Ingredient, Double> quantityColumn;


    @FXML
    private TableColumn<Ingredient, String> unitColumn;


    @FXML
    private TableColumn<Ingredient, Double> minimumColumn;



    private final IngredientService ingredientService =
            new IngredientService();



    @FXML
    public void initialize() {


        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );


        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );


        unitColumn.setCellValueFactory(
                new PropertyValueFactory<>("unit")
        );


        minimumColumn.setCellValueFactory(
                new PropertyValueFactory<>("minimumStock")
        );


        loadIngredients();

    }





    private void loadIngredients() {


        ingredientTable.setItems(

                FXCollections.observableArrayList(

                        ingredientService.getAllIngredients()

                )

        );


    }





    @FXML
    private void restockIngredient() {


        Ingredient ingredient =
                ingredientTable.getSelectionModel()
                        .getSelectedItem();



        if (ingredient == null) {

            System.out.println(
                    "Please select an ingredient."
            );

            return;

        }



        TextInputDialog dialog =
                new TextInputDialog();



        dialog.setTitle(
                "Restock Ingredient"
        );


        dialog.setHeaderText(
                "Restock " + ingredient.getName()
        );


        dialog.setContentText(
                "Quantity to add:"
        );



        Optional<String> result =
                dialog.showAndWait();



        if(result.isPresent()) {


            try {


                double amount =
                        Double.parseDouble(
                                result.get()
                        );



                ingredientService.addStock(
                        ingredient.getId(),
                        amount
                );



                loadIngredients();



                System.out.println(
                        "Ingredient restocked successfully."
                );


            }
            catch(NumberFormatException e) {


                System.out.println(
                        "Please enter a valid number."
                );


            }


        }


    }





    @FXML
    private void openHistory() {


        Navigator.goTo(
                "inventory_history.fxml"
        );


    }





    @FXML
    private void goBack() {


        Navigator.goBack();


    }


}