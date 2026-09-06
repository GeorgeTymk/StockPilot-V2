package com.stockpilot.controller;

import com.stockpilot.model.Sale;
import com.stockpilot.service.SaleService;
import com.stockpilot.util.Navigator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class FilteredSalesReportController {

    private static LocalDate selectedFrom;
    private static LocalDate selectedTo;
    private static String selectedPeriod = "All Time";

    public static void setFilter(
            LocalDate from,
            LocalDate to,
            String period
    ) {
        selectedFrom = from;
        selectedTo = to;
        selectedPeriod = period;
    }

    @FXML
    private Label periodLabel;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private TableView<SaleRow> salesTable;

    @FXML
    private TableColumn<SaleRow, String> dateColumn;

    @FXML
    private TableColumn<SaleRow, String> recipeColumn;

    @FXML
    private TableColumn<SaleRow, Integer> quantityColumn;

    @FXML
    private TableColumn<SaleRow, Double> totalColumn;

    private final SaleService saleService =
            new SaleService();

    @FXML
    public void initialize() {

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        recipeColumn.setCellValueFactory(
                new PropertyValueFactory<>("recipe")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );

        loadReport();
    }

    private void loadReport() {

        String period =
                selectedPeriod == null
                        ? "All Time"
                        : selectedPeriod;

        if (periodLabel != null) {

            periodLabel.setText(
                    "Sales Report — " + period
            );
        }

        /*
         * The database performs the date filtering.
         */
        List<Sale> sales =
                saleService.getSalesBetweenRecords(
                        selectedFrom,
                        selectedTo
                );

        double salesTotal = 0;
        int orderCount = 0;
        int itemCount = 0;

        var rows =
                FXCollections.<SaleRow>observableArrayList();

        for (Sale sale : sales) {

            rows.add(
                    new SaleRow(
                            sale.getSaleDate(),
                            sale.getRecipeName(),
                            sale.getQuantity(),
                            sale.getTotal()
                    )
            );

            salesTotal += sale.getTotal();
            orderCount++;
            itemCount += sale.getQuantity();
        }

        salesTable.setItems(rows);

        totalSalesLabel.setText(
                "MK "
                + String.format(
                        Locale.US,
                        "%,.2f",
                        salesTotal
                )
        );

        totalOrdersLabel.setText(
                String.valueOf(orderCount)
        );

        totalItemsLabel.setText(
                String.valueOf(itemCount)
        );

        System.out.println(
                "Filtered sales records: "
                        + sales.size()
        );
    }

    @FXML
    private void refresh() {
        loadReport();
    }

    @FXML
    private void goBack() {
        Navigator.goBack();
    }

    public static class SaleRow {

        private final String date;
        private final String recipe;
        private final int quantity;
        private final double total;

        public SaleRow(
                String date,
                String recipe,
                int quantity,
                double total
        ) {
            this.date = date;
            this.recipe = recipe;
            this.quantity = quantity;
            this.total = total;
        }

        public String getDate() {
            return date;
        }

        public String getRecipe() {
            return recipe;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotal() {
            return total;
        }
    }
}
