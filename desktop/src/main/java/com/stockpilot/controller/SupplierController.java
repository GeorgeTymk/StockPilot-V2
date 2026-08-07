package com.stockpilot.controller;

import com.stockpilot.model.Supplier;
import com.stockpilot.service.SupplierService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SupplierController {


    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> idColumn;

    @FXML
    private TableColumn<Supplier, String> nameColumn;

    @FXML
    private TableColumn<Supplier, String> phoneColumn;

    @FXML
    private TableColumn<Supplier, String> emailColumn;


    private final SupplierService supplierService =
            new SupplierService();


    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        loadSuppliers();

    }


    private void loadSuppliers() {

        supplierTable.setItems(

                FXCollections.observableArrayList(

                        supplierService.getAllSuppliers()

                )

        );

    }


    @FXML
    private void addSupplier() {

        Navigator.goTo(
                "add_supplier.fxml"
        );

    }


    /*
     * Return to the main application shell
     * (Sidebar + Dashboard)
     */
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
    private void goBack() {

        Navigator.goBack();

    }

}