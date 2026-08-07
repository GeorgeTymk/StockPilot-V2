package com.stockpilot.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;

import com.stockpilot.util.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;



public class SidebarController {

@FXML
private void openAbout(){

    try {

        Parent root =
                FXMLLoader.load(
                    getClass()
                    .getResource("/fxml/about.fxml")
                );


        Stage stage = new Stage();

        stage.setTitle("About StockPilot");

        stage.initModality(
                Modality.APPLICATION_MODAL
        );


        Scene scene = new Scene(root);


scene.getStylesheets().add(
        getClass()
        .getResource("/css/stockpilot.css")
        .toExternalForm()
);


stage.setScene(scene);

stage.setWidth(420);
stage.setHeight(330);
stage.centerOnScreen();


        stage.setResizable(false);

        stage.show();


    }
    catch(Exception e){

        e.printStackTrace();

    }

}

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button inventoryBtn;

    @FXML
    private Button ingredientsBtn;

    @FXML
    private Button recipesBtn;

    @FXML
    private Button suppliersBtn;

    @FXML
    private Button salesBtn;

    @FXML
    private Button salesHistoryBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button lowStockBtn;




    @FXML
    private VBox dashboardNav;

    @FXML
    private VBox inventoryNav;

    @FXML
    private VBox ingredientsNav;

    @FXML
    private VBox recipesNav;

    @FXML
    private VBox suppliersNav;

    @FXML
    private VBox salesNav;

    @FXML
    private VBox salesHistoryNav;

    @FXML
    private VBox reportsNav;

    @FXML
    private VBox lowStockNav;








    private void loadPage(String page){


        MainShellController shell =
                MainShellController.getInstance();



        if(shell != null){


            shell.loadPage(
                    "/fxml/" + page
            );


            setActive(
                    page.replace(".fxml","")
            );


        }


    }








    @FXML
    private void openDashboard(){

        loadPage(
                "dashboard.fxml"
        );

    }






    @FXML
    private void openInventory(){

        loadPage(
                "inventory.fxml"
        );

    }






    @FXML
    private void openIngredients(){

        loadPage(
                "ingredients.fxml"
        );

    }






    @FXML
    private void openRecipes(){

        loadPage(
                "recipes.fxml"
        );

    }






    @FXML
    private void openSuppliers(){

        loadPage(
                "suppliers.fxml"
        );

    }






    @FXML
    private void openSales(){

        loadPage(
                "sales.fxml"
        );

    }






    @FXML
    private void openSalesHistory(){

        loadPage(
                "sales_history.fxml"
        );

    }






    @FXML
    private void openReports(){

        loadPage(
                "reports.fxml"
        );

    }






    @FXML
    private void openLowStock(){

        loadPage(
                "low_stock.fxml"
        );

    }






    @FXML
    private void logout(){


        Navigator.goTo(
                "login.fxml"
        );


    }









    private void clearActive(){


        dashboardNav.getStyleClass().remove("nav-active");
        inventoryNav.getStyleClass().remove("nav-active");
        ingredientsNav.getStyleClass().remove("nav-active");
        recipesNav.getStyleClass().remove("nav-active");
        suppliersNav.getStyleClass().remove("nav-active");
        salesNav.getStyleClass().remove("nav-active");
        salesHistoryNav.getStyleClass().remove("nav-active");
        reportsNav.getStyleClass().remove("nav-active");
        lowStockNav.getStyleClass().remove("nav-active");


    }









    public void setActive(String page){


        clearActive();



        switch(page){


            case "dashboard" ->
                    dashboardNav.getStyleClass()
                            .add("nav-active");


            case "inventory" ->
                    inventoryNav.getStyleClass()
                            .add("nav-active");


            case "ingredients" ->
                    ingredientsNav.getStyleClass()
                            .add("nav-active");


            case "recipes" ->
                    recipesNav.getStyleClass()
                            .add("nav-active");


            case "suppliers" ->
                    suppliersNav.getStyleClass()
                            .add("nav-active");


            case "sales" ->
                    salesNav.getStyleClass()
                            .add("nav-active");


            case "sales_history" ->
                    salesHistoryNav.getStyleClass()
                            .add("nav-active");


            case "reports" ->
                    reportsNav.getStyleClass()
                            .add("nav-active");


            case "low_stock" ->
                    lowStockNav.getStyleClass()
                            .add("nav-active");


        }


    }



}