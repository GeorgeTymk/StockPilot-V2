package com.stockpilot.controller;


import com.stockpilot.model.Sale;
import com.stockpilot.service.SaleService;
import com.stockpilot.util.Navigator;

import com.stockpilot.controller.MainShellController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;



public class SalesHistoryController {



    @FXML
    private TableView<Sale> salesTable;



    @FXML
    private TableColumn<Sale,Integer> idColumn;



    @FXML
    private TableColumn<Sale,String> recipeColumn;



    @FXML
    private TableColumn<Sale,Integer> quantityColumn;



    @FXML
    private TableColumn<Sale,Double> totalColumn;



    @FXML
    private TableColumn<Sale,String> dateColumn;





    private final SaleService saleService =
            new SaleService();







    @FXML
    public void initialize(){



        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );



        recipeColumn.setCellValueFactory(
                new PropertyValueFactory<>("recipeName")
        );



        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );



        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );



        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("saleDate")
        );



        loadSales();


    }







    private void loadSales(){



        salesTable.setItems(

                FXCollections.observableArrayList(

                        saleService.getAllSales()

                )

        );


    }







    @FXML
    private void refreshSales(){


        loadSales();


    }







     @FXML
private void openDashboard() {

    MainShellController shell = MainShellController.getInstance();

    if (shell != null) {

        // Already inside the shell
        shell.loadPage("/fxml/dashboard.fxml");

    } else {

        // Coming from login or another standalone window
        Navigator.goTo("shell/MainShell.fxml");

    }
}







    @FXML
    private void goBack(){


        Navigator.goBack();


    }



}