package com.stockpilot.controller;


import com.stockpilot.model.InventoryHistory;
import com.stockpilot.service.InventoryHistoryService;
import com.stockpilot.util.Navigator;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class InventoryHistoryController {



    @FXML
    private TableView<InventoryHistory> historyTable;



    @FXML
    private TableColumn<InventoryHistory, Integer> idColumn;



    @FXML
    private TableColumn<InventoryHistory, String> ingredientColumn;



    @FXML
    private TableColumn<InventoryHistory, String> movementColumn;



    @FXML
    private TableColumn<InventoryHistory, Double> quantityColumn;



    @FXML
    private TableColumn<InventoryHistory, String> dateColumn;





    private final InventoryHistoryService service =
            new InventoryHistoryService();






    @FXML
    public void initialize(){



        if(idColumn != null){

            idColumn.setCellValueFactory(
                    new PropertyValueFactory<>("id")
            );

        }




        if(ingredientColumn != null){

            ingredientColumn.setCellValueFactory(
                    new PropertyValueFactory<>("ingredientName")
            );

        }




        if(movementColumn != null){

            movementColumn.setCellValueFactory(
                    new PropertyValueFactory<>("movementType")
            );

        }




        if(quantityColumn != null){

            quantityColumn.setCellValueFactory(
                    new PropertyValueFactory<>("quantity")
            );

        }




        if(dateColumn != null){

            dateColumn.setCellValueFactory(
                    new PropertyValueFactory<>("createdAt")
            );

        }



        loadHistory();


    }







    private void loadHistory(){



        if(historyTable == null){

            System.out.println(
                    "History table not connected in FXML"
            );

            return;

        }



        historyTable.setItems(

                FXCollections.observableArrayList(

                        service.getHistory()

                )

        );


    }







    @FXML
    private void goBack(){


        Navigator.goBack();


    }



}