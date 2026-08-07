package com.stockpilot.controller;


import com.stockpilot.model.Recipe;
import com.stockpilot.service.RecipeService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class RecipeController {



    @FXML
    private TableView<Recipe> recipeTable;



    @FXML
    private TableColumn<Recipe, Integer> idColumn;



    @FXML
    private TableColumn<Recipe, String> nameColumn;



    @FXML
    private TableColumn<Recipe, String> descriptionColumn;



    @FXML
    private TableColumn<Recipe, Double> priceColumn;




    private final RecipeService recipeService =
            new RecipeService();



    // Shared selected recipe
    public static Recipe selectedRecipe;





    @FXML
    public void initialize() {


        setupColumns();

        loadRecipes();


    }





    private void setupColumns() {


        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );


        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );


        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );


        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("sellingPrice")
        );


    }





    private void loadRecipes() {


        recipeTable.setItems(

                FXCollections.observableArrayList(

                        recipeService.getAllRecipes()

                )

        );


    }







    @FXML
private void addRecipe() {

    MainShellController shell =
            MainShellController.getInstance();

    if (shell != null) {

        shell.loadPage("/fxml/add_recipe.fxml");

    } else {

        System.out.println("MainShell not found.");

    }
}







    @FXML
    private void viewRecipe() {


        Recipe recipe =
                recipeTable
                        .getSelectionModel()
                        .getSelectedItem();



        if(recipe == null) {


            showAlert(
                    Alert.AlertType.WARNING,
                    "No Recipe Selected",
                    "Please select a recipe before viewing details."
            );


            return;

        }




        selectedRecipe = recipe;




        Navigator.goTo(
                "recipe_details.fxml"
        );


    }
















    @FXML
    private void goBack() {


        Navigator.goBack();


    }








    public void refresh() {


        loadRecipes();


    }








    private void showAlert(

            Alert.AlertType type,

            String title,

            String message

    ) {



        Alert alert =
                new Alert(type);



        alert.setTitle(title);



        alert.setHeaderText(null);



        alert.setContentText(message);



        alert.showAndWait();



    }




}