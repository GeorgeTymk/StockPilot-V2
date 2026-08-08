package com.stockpilot.controller;


import com.stockpilot.database.Database;
import com.stockpilot.model.Ingredient;
import com.stockpilot.service.RecipeIngredientService;
import com.stockpilot.util.Navigator;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;



public class AddRecipeIngredientController {



    @FXML
    private ComboBox<Ingredient> ingredientComboBox;



    @FXML
    private TextField quantityField;



    @FXML
    private Label unitLabel;




    private final RecipeIngredientService service =
            new RecipeIngredientService();



    private final List<Ingredient> ingredients =
            new ArrayList<>();






    @FXML
    public void initialize(){


        loadIngredients();



        ingredientComboBox.setOnAction(event -> {


            Ingredient ingredient =
                    ingredientComboBox.getValue();



            if(ingredient != null){


                unitLabel.setText(
                        "Unit: "
                        +
                        ingredient.getUnit()
                );


            }


        });


    }







    private void loadIngredients(){



        String sql =
                "SELECT * FROM ingredients";



        try(
                Connection connection =
                        Database.connect();


                Statement statement =
                        connection.createStatement();


                ResultSet result =
                        statement.executeQuery(sql)

        ){



            while(result.next()){



                ingredients.add(

                        new Ingredient(

                                result.getInt("id"),

                                result.getString("name"),

                                result.getDouble("quantity"),

                                result.getString("unit"),

                                result.getDouble("minimum_stock")

                        )

                );


            }





            ingredientComboBox.setItems(

                    FXCollections.observableArrayList(
                            ingredients
                    )

            );





        }
        catch(Exception e){


            e.printStackTrace();


        }


    }









    @FXML
    private void saveIngredient(){



        Ingredient ingredient =
                ingredientComboBox.getValue();




        if(ingredient == null){


            System.out.println(
                    "Please select an ingredient"
            );


            return;


        }





        if(quantityField.getText().isBlank()){


            System.out.println(
                    "Enter quantity"
            );


            return;


        }






        try{


            double quantity =
                    Double.parseDouble(
                            quantityField.getText()
                    );






            service.addIngredientToRecipe(

                    RecipeController.selectedRecipe.getId(),

                    ingredient.getId(),

                    quantity

            );






            System.out.println(
                    "Ingredient added successfully"
            );



            Navigator.goBack();



        }
        catch(NumberFormatException e){


            System.out.println(
                    "Invalid quantity"
            );


        }



    }







    @FXML
    private void goBack(){


        Navigator.goBack();


    }


}