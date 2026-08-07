package com.stockpilot.controller;

import com.stockpilot.model.Recipe;
import com.stockpilot.service.RecipeService;
import com.stockpilot.service.SaleService;
import com.stockpilot.util.Navigator;

import com.stockpilot.controller.MainShellController;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class SalesController {


    @FXML
    private ComboBox<Recipe> recipeComboBox;

    @FXML
    private TextField quantityField;

    @FXML
    private Label totalLabel;

    @FXML
    private Button saveSaleButton;


    private final RecipeService recipeService =
            new RecipeService();

    private final SaleService saleService =
            new SaleService();


    @FXML
    public void initialize() {

        recipeComboBox.setItems(

                FXCollections.observableArrayList(

                        recipeService.getAllRecipes()

                )

        );

        recipeComboBox.setOnAction(
                e -> calculateTotal()
        );

        quantityField.textProperty()
                .addListener(

                        (obs, oldValue, newValue)
                                -> calculateTotal()

                );

    }


    @FXML
    private void calculateTotal() {

        Recipe recipe =
                recipeComboBox.getValue();

        if (recipe == null) {

            totalLabel.setText("0");

            return;

        }

        try {

            int quantity =
                    Integer.parseInt(
                            quantityField.getText()
                    );

            if (quantity <= 0) {

                totalLabel.setText("0");

                return;

            }

            double total =
                    recipe.getSellingPrice()
                            * quantity;

            totalLabel.setText(

                    String.format(
                            "%.2f",
                            total
                    )

            );

        }
        catch (Exception e) {

            totalLabel.setText("0");

        }

    }


    @FXML
    private void saveSale() {

        Recipe recipe =
                recipeComboBox.getValue();

        if (recipe == null) {

            showAlert(
                    "No Recipe",
                    "Please select a recipe.",
                    Alert.AlertType.WARNING
            );

            return;

        }

        int quantity;

        try {

            quantity =
                    Integer.parseInt(
                            quantityField.getText()
                    );

            if (quantity <= 0) {

                throw new Exception();

            }

        }
        catch (Exception e) {

            showAlert(
                    "Invalid Quantity",
                    "Enter a valid quantity.",
                    Alert.AlertType.WARNING
            );

            return;

        }

        double total =
                recipe.getSellingPrice()
                        * quantity;

        saveSaleButton.setDisable(true);
        saveSaleButton.setText("Saving...");

        ProgressIndicator loader =
                new ProgressIndicator();

        loader.setPrefSize(20, 20);

        saveSaleButton.setGraphic(loader);

        Task<Boolean> task =
                new Task<>() {

                    @Override
                    protected Boolean call() {

                        return saleService.saveSale(

                                recipe.getId(),

                                quantity,

                                total

                        );

                    }

                };

        task.setOnSucceeded(e -> {

            saveSaleButton.setDisable(false);
            saveSaleButton.setText("Save Sale");
            saveSaleButton.setGraphic(null);

            if (task.getValue()) {

                quantityField.clear();

                recipeComboBox
                        .getSelectionModel()
                        .clearSelection();

                totalLabel.setText("0");

                showAlert(

                        "Sold! ✅",

                        recipe.getName()
                                + " sold successfully.\n\n"
                                + "Quantity: "
                                + quantity
                                + "\nTotal: MK "
                                + String.format("%,.2f", total),

                        Alert.AlertType.INFORMATION

                );

            } else {

                showAlert(

                        "Sale Failed",

                        "Could not complete sale.",

                        Alert.AlertType.ERROR

                );

            }

        });

        task.setOnFailed(e -> {

            saveSaleButton.setDisable(false);
            saveSaleButton.setText("Save Sale");
            saveSaleButton.setGraphic(null);

            showAlert(

                    "Error",

                    "Something went wrong.",

                    Alert.AlertType.ERROR

            );

        });

        Thread thread =
                new Thread(task);

        thread.setDaemon(true);

        thread.start();

    }


    private void showAlert(

            String title,

            String message,

            Alert.AlertType type

    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();

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