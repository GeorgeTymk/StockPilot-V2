package com.stockpilot.controller;


import com.stockpilot.model.Recipe;
import com.stockpilot.service.RecipeService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;



public class AddRecipeController {


    @FXML
    private TextField nameField;


    @FXML
    private TextArea descriptionField;


    @FXML
    private TextField priceField;



    private final RecipeService recipeService =
            new RecipeService();






    /*
     * Load pages through MainShell
     * Keeps sidebar visible
     */
    private void loadPage(String page){


        MainShellController shell =
                MainShellController.getInstance();



        if(shell != null){


            shell.loadPage(
                    "/fxml/" + page
            );


        }
        else{


            System.out.println(
                    "MainShellController not available"
            );


        }


    }









    /*
     * Dashboard button
     */
    @FXML
    private void openDashboard(){


        loadPage(
                "dashboard.fxml"
        );


    }









    /*
     * Back to recipes
     */
    @FXML
    private void goBack(){


        loadPage(
                "recipes.fxml"
        );


    }









    /*
     * Save recipe
     */
    @FXML
    private void saveRecipe(){



        try{



            String name =
                    nameField.getText()
                            .trim();



            String description =
                    descriptionField.getText()
                            .trim();




            String priceText =
                    priceField.getText()
                            .trim();







            if(name.isEmpty()
                    || priceText.isEmpty()){



                showAlert(
                        Alert.AlertType.WARNING,
                        "Missing Information",
                        "Recipe name and selling price are required."
                );


                return;


            }








            double price =
                    Double.parseDouble(
                            priceText
                    );









            Recipe recipe =
                    new Recipe(

                            0,

                            name,

                            description,

                            price

                    );








            recipeService.addRecipe(recipe);








            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Recipe saved successfully."
            );








            /*
             * Clear form after save
             */

            nameField.clear();

            descriptionField.clear();

            priceField.clear();








            /*
             * Return to recipes page
             * Sidebar remains
             */

            loadPage(
                    "recipes.fxml"
            );







        }
        catch(NumberFormatException e){



            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Price",
                    "Selling price must contain numbers only."
            );


        }
        catch(Exception e){



            e.printStackTrace();



            showAlert(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Could not save recipe."
            );


        }



    }









    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ){


        Alert alert =
                new Alert(type);



        alert.setTitle(title);


        alert.setHeaderText(null);


        alert.setContentText(message);


        alert.showAndWait();



    }




}