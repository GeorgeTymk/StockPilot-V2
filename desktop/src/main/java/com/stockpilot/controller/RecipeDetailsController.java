package com.stockpilot.controller;


import com.stockpilot.model.Recipe;
import com.stockpilot.model.RecipeIngredient;
import com.stockpilot.service.RecipeIngredientService;
import com.stockpilot.util.Navigator;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class RecipeDetailsController {



    @FXML
    private Label recipeNameLabel;



    @FXML
    private Label sellingPriceLabel;



    @FXML
    private TableView<RecipeIngredient> ingredientTable;



    @FXML
    private TableColumn<RecipeIngredient, String> nameColumn;



    @FXML
    private TableColumn<RecipeIngredient, Double> quantityColumn;



    @FXML
    private TableColumn<RecipeIngredient, String> unitColumn;




    private final RecipeIngredientService recipeIngredientService =
            new RecipeIngredientService();






    /*
     * RETURN TO MAIN APPLICATION DASHBOARD
     * Loads the complete shell:
     * Sidebar + Dashboard
     */
    @FXML
    private void goDashboard(){


        Navigator.goTo(
                "shell/MainShell.fxml"
        );


    }







    @FXML
    public void initialize(){


        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("ingredientName")
        );


        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );


        unitColumn.setCellValueFactory(
                new PropertyValueFactory<>("unit")
        );



        loadRecipe();


    }









    private void loadRecipe(){



        Recipe recipe =
                RecipeController.selectedRecipe;



        if(recipe == null){


            System.out.println(
                    "No recipe selected"
            );


            return;


        }





        recipeNameLabel.setText(
                recipe.getName()
        );





        sellingPriceLabel.setText(

                String.format(
                        "MK %.2f",
                        recipe.getSellingPrice()
                )

        );






        ingredientTable.setItems(


                FXCollections.observableArrayList(

                        recipeIngredientService
                                .getRecipeIngredients(
                                        recipe.getId()
                                )

                )


        );



        System.out.println(
                "Recipe ingredients loaded: "
                + ingredientTable.getItems().size()
        );



    }









    @FXML
    private void addIngredient(){



        Navigator.goTo(
                "add_recipe_ingredient.fxml"
        );



    }









    @FXML
    private void removeIngredient(){



        RecipeIngredient ingredient =

                ingredientTable
                        .getSelectionModel()
                        .getSelectedItem();





        if(ingredient == null){



            System.out.println(
                    "Please select an ingredient."
            );



            return;


        }






        recipeIngredientService
                .removeIngredientFromRecipe(
                        ingredient.getId()
                );






        loadRecipe();



    }









    @FXML
    private void goBack(){



        Navigator.goBack();



    }



}