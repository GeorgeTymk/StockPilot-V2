
package com.stockpilot.controller;

import com.stockpilot.model.Ingredient;
import com.stockpilot.service.IngredientService;
import com.stockpilot.util.Navigator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

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


    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize() {

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


        // =================================================
        // STATUS COLUMN
        // =================================================

        statusColumn.setCellValueFactory(cellData -> {

            Ingredient ingredient =
                    cellData.getValue();

            double quantity =
                    ingredient.getQuantity();

            double minimum =
                    ingredient.getMinimumStock();

            String status;

            if (quantity <= 0) {

                status = "OUT OF STOCK";

            }
            else if (quantity <= minimum) {

                status = "LOW";

            }
            else {

                status = "GOOD";

            }

            return new SimpleStringProperty(status);

        });


        styleStatusColumn();

        styleIngredientRows();


        // Refresh row styling whenever selection changes
        ingredientTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (obs, oldSelection, newSelection) ->
                                ingredientTable.refresh()
                );


        loadIngredients();

    }


    // =====================================================
    // LOAD INGREDIENTS
    // =====================================================

    private void loadIngredients() {

        ingredientTable.setItems(

                FXCollections.observableArrayList(

                        ingredientService.getAllIngredients()

                )

        );

    }


    // =====================================================
    // ADD INGREDIENT
    // =====================================================

    @FXML
    private void addIngredient() {

        Navigator.goTo(
                "add_ingredient.fxml"
        );

    }


    // =====================================================
    // EDIT INGREDIENT
    // =====================================================

    @FXML
    private void editIngredient() {

        Ingredient selectedIngredient =
                ingredientTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedIngredient == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Ingredient Selected",
                    "Please select an ingredient to edit."
            );

            return;
        }


        Dialog<ButtonType> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Edit Ingredient"
        );


        dialog.setHeaderText(
                "Edit: "
                        + selectedIngredient.getName()
        );


        DialogPane dialogPane =
                dialog.getDialogPane();


        ButtonType saveButton =
                new ButtonType(
                        "Save",
                        javafx.scene.control.ButtonBar.ButtonData.OK_DONE
                );


        dialogPane.getButtonTypes().addAll(
                saveButton,
                ButtonType.CANCEL
        );


        GridPane grid =
                new GridPane();


        grid.setHgap(10);

        grid.setVgap(10);


        TextField nameField =
                new TextField(
                        selectedIngredient.getName()
                );


        TextField quantityField =
                new TextField(
                        String.valueOf(
                                selectedIngredient.getQuantity()
                        )
                );


        TextField unitField =
                new TextField(
                        selectedIngredient.getUnit()
                );


        TextField minimumField =
                new TextField(
                        String.valueOf(
                                selectedIngredient.getMinimumStock()
                        )
                );


        grid.add(
                new Label("Name:"),
                0,
                0
        );


        grid.add(
                nameField,
                1,
                0
        );


        grid.add(
                new Label("Quantity:"),
                0,
                1
        );


        grid.add(
                quantityField,
                1,
                1
        );


        grid.add(
                new Label("Unit:"),
                0,
                2
        );


        grid.add(
                unitField,
                1,
                2
        );


        grid.add(
                new Label("Minimum Stock:"),
                0,
                3
        );


        grid.add(
                minimumField,
                1,
                3
        );


        dialogPane.setContent(grid);


        Optional<ButtonType> result =
                dialog.showAndWait();


        if (result.isEmpty()
                || result.get() != saveButton) {

            return;
        }


        try {

            String name =
                    nameField
                            .getText()
                            .trim();


            String unit =
                    unitField
                            .getText()
                            .trim();


            double quantity =
                    Double.parseDouble(
                            quantityField
                                    .getText()
                                    .trim()
                    );


            double minimumStock =
                    Double.parseDouble(
                            minimumField
                                    .getText()
                                    .trim()
                    );


            if (name.isEmpty()
                    || unit.isEmpty()
                    || quantity < 0
                    || minimumStock < 0) {

                throw new IllegalArgumentException();

            }


            selectedIngredient.setName(
                    name
            );


            selectedIngredient.setQuantity(
                    quantity
            );


            selectedIngredient.setUnit(
                    unit
            );


            selectedIngredient.setMinimumStock(
                    minimumStock
            );


            Ingredient updated =
                    ingredientService.updateIngredient(
                            selectedIngredient
                    );


            if (updated == null) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Update Failed",
                        "The ingredient could not be updated."
                );

                return;
            }


            loadIngredients();


            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Ingredient Updated",
                    name
                            + " was updated successfully."
            );

        }
        catch (NumberFormatException e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Number",
                    "Quantity and Minimum Stock must be valid numbers."
            );

        }
        catch (IllegalArgumentException e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Information",
                    "Please enter valid ingredient information."
            );

        }
        catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Update Failed",
                    "An error occurred while updating the ingredient."
            );

            e.printStackTrace();

        }

    }


    // =====================================================
    // DELETE INGREDIENT
    // =====================================================

    @FXML
    private void deleteIngredient() {

        Ingredient selectedIngredient =
                ingredientTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedIngredient == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Ingredient Selected",
                    "Please select an ingredient to delete."
            );

            return;
        }


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Ingredient"
        );


        confirmation.setHeaderText(
                "Delete "
                        + selectedIngredient.getName()
                        + "?"
        );


        confirmation.setContentText(
                "This action cannot be undone."
        );


        Optional<ButtonType> result =
                confirmation.showAndWait();


        if (result.isEmpty()
                || result.get() != ButtonType.OK) {

            return;
        }


        try {

            ingredientService.deleteIngredient(
                    selectedIngredient.getId()
            );


            loadIngredients();


            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Ingredient Deleted",
                    selectedIngredient.getName()
                            + " was deleted successfully."
            );

        }
        catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Delete Failed",
                    "The ingredient could not be deleted."
            );

            e.printStackTrace();

        }

    }


    // =====================================================
    // RESTOCK INGREDIENT
    // =====================================================

    @FXML
    private void restockIngredient() {

        Ingredient selectedIngredient =
                ingredientTable
                        .getSelectionModel()
                        .getSelectedItem();


        if (selectedIngredient == null) {

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


        if (result.isPresent()) {

            try {

                double quantity =
                        Double.parseDouble(
                                result.get()
                        );


                if (quantity <= 0) {

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
            catch (NumberFormatException e) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Invalid Quantity",
                        "Please enter a valid positive number."
                );

            }

        }

    }


    // =====================================================
    // STATUS COLUMN STYLING
    // =====================================================

    private void styleStatusColumn() {

        statusColumn.setCellFactory(column ->

                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty
                    ) {

                        super.updateItem(
                                status,
                                empty
                        );


                        if (empty || status == null) {

                            setText(null);

                            setStyle("");

                        }
                        else {

                            setText(status);


                            if (status.equals(
                                    "OUT OF STOCK"
                            )) {

                                setStyle(
                                        "-fx-text-fill:red;"
                                );

                            }
                            else if (status.equals(
                                    "LOW"
                            )) {

                                setStyle(
                                        "-fx-text-fill:orange;"
                                );

                            }
                            else {

                                setStyle(
                                        "-fx-text-fill:green;"
                                );

                            }

                        }

                    }

                }

        );

    }


    // =====================================================
    // INGREDIENT ROW STYLING
    // =====================================================

    private void styleIngredientRows() {

        ingredientTable.setRowFactory(table ->

                new TableRow<>() {

                    @Override
                    protected void updateItem(
                            Ingredient ingredient,
                            boolean empty
                    ) {

                        super.updateItem(
                                ingredient,
                                empty
                        );


                        if (empty || ingredient == null) {

                            setStyle("");

                            return;
                        }


                        // =================================================
                        // SELECTED ROW
                        // =================================================

                        if (isSelected()) {

                            setStyle(
                                    "-fx-background-color:#4f6f52;"
                                    + "-fx-text-fill:white;"
                            );

                            return;
                        }


                        double quantity =
                                ingredient.getQuantity();


                        double minimum =
                                ingredient.getMinimumStock();


                        // =================================================
                        // OUT OF STOCK
                        // =================================================

                        if (quantity <= 0) {

                            setStyle(
                                    "-fx-background-color:#ffcccc;"
                            );

                        }


                        // =================================================
                        // LOW STOCK
                        // =================================================

                        else if (quantity < minimum) {

                            setStyle(
                                    "-fx-background-color:#ffe6b3;"
                            );

                        }


                        // =================================================
                        // GOOD STOCK
                        // =================================================

                        else {

                            setStyle(
                                    "-fx-background-color:#ccffcc;"
                            );

                        }

                    }

                }

        );

    }


    // =====================================================
    // ALERT
    // =====================================================

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


    // =====================================================
    // INVENTORY HISTORY
    // =====================================================

    @FXML
    private void openHistory() {

        Navigator.goTo(
                "inventory_history.fxml"
        );

    }


    // =====================================================
    // GO BACK
    // =====================================================

    @FXML
    private void goBack() {

        Navigator.goBack();

    }

}
  
