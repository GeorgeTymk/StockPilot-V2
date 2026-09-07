package com.stockpilot.controller;

import com.stockpilot.model.Sale;
import com.stockpilot.service.SaleService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class SalesHistoryController {

    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, Integer> idColumn;

    @FXML
    private TableColumn<Sale, String> recipeColumn;

    @FXML
    private TableColumn<Sale, Integer> quantityColumn;

    @FXML
    private TableColumn<Sale, Double> totalColumn;

    @FXML
    private TableColumn<Sale, String> dateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> periodFilter;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label periodLabel;

    private final SaleService saleService =
            new SaleService();

    private LocalDate customFrom;

    private LocalDate customTo;


    @FXML
    public void initialize(){

        setupColumns();

        setupPeriodFilter();

        if(searchField != null){

            searchField.textProperty()
                    .addListener(
                            (observable, oldValue, newValue) ->
                                    applyFilters()
                    );

        }

        loadSales();

    }


    private void setupColumns(){

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

    }


    private void setupPeriodFilter(){

        if(periodFilter == null){
            return;
        }

        periodFilter.getItems().setAll(
                "All Time",
                "Today",
                "Last 7 Days",
                "Last 14 Days",
                "Last 30 Days",
                "This Month",
                "Custom Range"
        );

        periodFilter.setValue("All Time");

        periodFilter.setOnAction(
                event -> {

                    if(
                            "Custom Range".equals(
                                    periodFilter.getValue()
                            )
                    ){

                        if(!chooseCustomRange()){

                            periodFilter.setValue(
                                    "All Time"
                            );

                            customFrom = null;
                            customTo = null;

                        }

                    }

                    applyFilters();

                }
        );

    }


    private void loadSales(){

        applyFilters();

    }


    private void applyFilters(){

        LocalDate from = null;

        LocalDate to = null;

        String selected =
                periodFilter == null
                        ? "All Time"
                        : periodFilter.getValue();


        LocalDate today =
                LocalDate.now();


        if("Today".equals(selected)){

            from = today;

            to = today;

        }
        else if(
                "Last 7 Days".equals(selected)
        ){

            from =
                    today.minusDays(6);

            to = today;

        }
        else if(
                "Last 14 Days".equals(selected)
        ){

            from =
                    today.minusDays(13);

            to = today;

        }
        else if(
                "Last 30 Days".equals(selected)
        ){

            from =
                    today.minusDays(29);

            to = today;

        }
        else if(
                "This Month".equals(selected)
        ){

            from =
                    today.withDayOfMonth(1);

            to = today;

        }
        else if(
                "Custom Range".equals(selected)
        ){

            from = customFrom;

            to = customTo;

        }


        List<Sale> sales =
                saleService.getSalesBetweenRecords(
                        from,
                        to
                );


        String search =
                searchField == null
                        ? ""
                        : searchField.getText();


        search =
                search == null
                        ? ""
                        : search.trim()
                                .toLowerCase();


        ObservableList<Sale> filtered =
                FXCollections.observableArrayList();


        double salesTotal = 0;

        int orderCount = 0;

        int itemCount = 0;


        for(Sale sale : sales){

            if(
                    !search.isBlank()
                    &&
                    (
                            sale.getRecipeName() == null
                            ||
                            !sale.getRecipeName()
                                    .toLowerCase()
                                    .contains(search)
                    )
            ){

                continue;

            }


            filtered.add(sale);

            salesTotal += sale.getTotal();

            orderCount++;

            itemCount += sale.getQuantity();

        }


        salesTable.setItems(
                filtered
        );


        if(totalSalesLabel != null){

            totalSalesLabel.setText(
                    "MK "
                    + String.format(
                            Locale.US,
                            "%,.2f",
                            salesTotal
                    )
            );

        }


        if(totalOrdersLabel != null){

            totalOrdersLabel.setText(
                    String.valueOf(
                            orderCount
                    )
            );

        }


        if(totalItemsLabel != null){

            totalItemsLabel.setText(
                    String.valueOf(
                            itemCount
                    )
            );

        }


        if(periodLabel != null){

            periodLabel.setText(
                    "Orders — " + selected
            );

        }


        System.out.println(
                "Orders displayed: "
                        + filtered.size()
        );

    }


    private boolean chooseCustomRange(){

        DatePicker fromPicker =
                new DatePicker(
                        customFrom == null
                                ? LocalDate.now()
                                        .minusDays(29)
                                : customFrom
                );


        DatePicker toPicker =
                new DatePicker(
                        customTo == null
                                ? LocalDate.now()
                                : customTo
                );


        GridPane grid =
                new GridPane();

        grid.setHgap(12);

        grid.setVgap(12);


        grid.add(
                new Label("From"),
                0,
                0
        );

        grid.add(
                fromPicker,
                1,
                0
        );


        grid.add(
                new Label("To"),
                0,
                1
        );

        grid.add(
                toPicker,
                1,
                1
        );


        javafx.scene.control.Dialog<ButtonType>
                dialog =
                new javafx.scene.control.Dialog<>();


        dialog.setTitle(
                "Custom Order Range"
        );

        dialog.setHeaderText(
                "Select the order period"
        );


        ButtonType apply =
                new ButtonType(
                        "Apply",
                        ButtonBar.ButtonData.OK_DONE
                );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        apply,
                        ButtonType.CANCEL
                );


        dialog.getDialogPane()
                .setContent(grid);


        var result =
                dialog.showAndWait();


        if(result.isEmpty()
                || result.get() != apply){

            return false;

        }


        LocalDate from =
                fromPicker.getValue();

        LocalDate to =
                toPicker.getValue();


        if(
                from == null
                || to == null
                || from.isAfter(to)
        ){

            return false;

        }


        customFrom = from;

        customTo = to;


        return true;

    }


    @FXML
    private void refreshSales(){

        loadSales();

    }


    @FXML
    private void clearFilters(){

        if(searchField != null){

            searchField.clear();

        }


        if(periodFilter != null){

            periodFilter.setValue(
                    "All Time"
            );

        }


        customFrom = null;

        customTo = null;

        loadSales();

    }


    @FXML
    private void openDashboard(){

        Navigator.goTo(
                "shell/MainShell.fxml"
        );

    }


    @FXML
    private void goBack(){

        Navigator.goBack();

    }

}