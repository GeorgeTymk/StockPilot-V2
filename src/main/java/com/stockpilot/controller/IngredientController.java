package com.stockpilot.controller;


import com.stockpilot.model.Ingredient;
import com.stockpilot.service.IngredientService;
import com.stockpilot.util.Navigator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;



public class IngredientController {



    @FXML
    private TableView<Ingredient> ingredientTable;


    @FXML
    private TableColumn<Ingredient, Integer> idColumn;


    @FXML
    private TableColumn<Ingredient, String> nameColumn;


    @FXML
    private TableColumn<Ingredient, Double> quantityColumn;


    @FXML
    private TableColumn<Ingredient, String> unitColumn;


    @FXML
    private TableColumn<Ingredient, Double> minimumColumn;


    @FXML
    private TableColumn<Ingredient, String> statusColumn;




    private final IngredientService ingredientService =
            new IngredientService();





    @FXML
    public void initialize(){


        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );


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



        statusColumn.setCellValueFactory(cellData -> {


            Ingredient ingredient =
                    cellData.getValue();


            double quantity =
                    ingredient.getQuantity();


            double minimum =
                    ingredient.getMinimumStock();



            String status;


            if(quantity <= 0){

    status = "OUT OF STOCK";

}
else if(quantity <= minimum){

    status = "LOW";

}
else{

    status = "GOOD";

}


            return new SimpleStringProperty(status);


        });



        styleStatusColumn();

        styleIngredientRows();


        loadIngredients();


    }







    private void loadIngredients(){


        ingredientTable.setItems(

                FXCollections.observableArrayList(

                        ingredientService.getAllIngredients()

                )

        );


    }









    @FXML
    private void addIngredient(){


        Navigator.goTo(
                "add_ingredient.fxml"
        );


    }









    @FXML
    private void restockIngredient(){


        Ingredient selectedIngredient =

                ingredientTable
                        .getSelectionModel()
                        .getSelectedItem();



        if(selectedIngredient == null){


            showAlert(
                    Alert.AlertType.WARNING,
                    "No Ingredient Selected",
                    "Please select an ingredient to restock."
            );


            return;

        }




        TextInputDialog dialog =
                new TextInputDialog();



        dialog.setTitle(
                "Restock Ingredient"
        );


        dialog.setHeaderText(
                "Adding stock for: "
                + selectedIngredient.getName()
        );


        dialog.setContentText(
                "Quantity:"
        );



        Optional<String> result =
                dialog.showAndWait();




        if(result.isPresent()){


            try{


                double quantity =
                        Double.parseDouble(
                                result.get()
                        );



                if(quantity <= 0){

                    throw new NumberFormatException();

                }



                ingredientService.addStock(

                        selectedIngredient.getId(),

                        quantity

                );



                loadIngredients();



                showAlert(

                        Alert.AlertType.INFORMATION,

                        "Restock Complete",

                        selectedIngredient.getName()
                        + " stock increased by "
                        + quantity

                );


            }
            catch(NumberFormatException e){


                showAlert(

                        Alert.AlertType.ERROR,

                        "Invalid Quantity",

                        "Please enter a valid positive number."

                );


            }


        }


    }









    private void styleStatusColumn(){



        statusColumn.setCellFactory(column ->

                new TableCell<>(){


                    @Override
                    protected void updateItem(

                            String status,

                            boolean empty

                    ){


                        super.updateItem(status, empty);



                        if(empty || status == null){


                            setText(null);

                            setStyle("");

                        }
                        else{


                            setText(status);



                            if(status.equals("OUT OF STOCK")){


    setStyle(
            "-fx-text-fill:red;"
    );


}
else if(status.equals("LOW")){


    setStyle(
            "-fx-text-fill:orange;"
    );


}
else{


    setStyle(
            "-fx-text-fill:green;"
    );


}


                        }


                    }


                }

        );


    }









    private void styleIngredientRows(){


    ingredientTable.setRowFactory(table ->

            new TableRow<>(){


                @Override
                protected void updateItem(

                        Ingredient ingredient,

                        boolean empty

                ){


                    super.updateItem(
                            ingredient,
                            empty
                    );



                    if(empty || ingredient == null){


                        setStyle("");

                        return;

                    }



                    double quantity =
                            ingredient.getQuantity();


                    double minimum =
                            ingredient.getMinimumStock();



                    // 🔴 OUT OF STOCK

                    if(quantity <= 0){


                        setStyle(
                                "-fx-background-color:#ffcccc;"
                        );


                    }


                    // 🟠 LOW STOCK

                    else if(quantity < minimum){


                        setStyle(
                                "-fx-background-color:#ffe6b3;"
                        );


                    }


                    // 🟢 GOOD STOCK

                    else{


                        setStyle(
                                "-fx-background-color:#ccffcc;"
                        );


                    }


                }


            }


    );


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



@FXML
private void openHistory() {

    Navigator.goTo("inventory_history.fxml");

}




    @FXML
    private void goBack(){


        Navigator.goBack();


    }


}