package com.stockpilot.controller;

import javafx.scene.paint.Color;

import com.stockpilot.service.IngredientService;
import com.stockpilot.service.SaleService;


import javafx.fxml.FXML;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Map;



public class ReportPreviewController {


    @FXML
    private Label dateLabel;


    @FXML
    private VBox contentBox;


    @FXML
    private StackPane chartContainer;


    @FXML
    private GridPane kpiGrid;



    private Stage stage;



    private final SaleService saleService =
            new SaleService();



    private final IngredientService ingredientService =
            new IngredientService();





    @FXML
    public void initialize(){


        dateLabel.setText(

                "Generated: "
                        +
                LocalDateTime.now().format(

                        DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy  HH:mm"
                        )

                )

        );


    }






    public void setStage(Stage stage){

        this.stage = stage;

    }






    public void setReport(String report){


    createKPICards();


    contentBox.getChildren().clear();



    String[] lines =
            report.split("\n");



    for(String line : lines){



        if(line.isBlank()){

            continue;

        }



        Label label =
                new Label(line);



        label.setWrapText(true);



        label.getStyleClass()
                .add("report-item");



        /*
         * SECTION COLORS
         */


        if(line.contains("LOW STOCK ITEMS")){


            label.getStyleClass()
                    .add("status-low");


        }



        else if(line.contains("OUT OF STOCK ITEMS")){


            label.getStyleClass()
                    .add("status-out");


        }



        /*
         * Ingredient names under sections
         */


        else if(line.contains(" - ")){

            if(
                line.matches(".*\\d+.*")
            ){

                label.setStyle(
                        "-fx-text-fill:#F57C00;"
                        +
                        "-fx-font-weight:bold;"
                );

            }

        }



        contentBox.getChildren()
                .add(label);



    }



    loadSalesChart();
    contentBox.requestLayout();


}








    // =====================================================
    // KPI CARDS
    // =====================================================


    private void createKPICards(){


        kpiGrid.getChildren().clear();



        addCard(
                "Revenue",
                "MK "
                +
                String.format(
                        "%,.2f",
                        saleService.getTotalSales()
                ),
                0,
                0
        );



        addCard(

                "Orders",

                String.valueOf(
                        saleService.getTotalOrders()
                ),

                1,
                0

        );



        addCard(

                "Low Stock",

                String.valueOf(
                        ingredientService.getLowStockCount()
                ),

                0,
                1

        );



        addCard(

                "Out Of Stock",

                String.valueOf(
                        ingredientService.getOutOfStockCount()
                ),

                1,
                1

        );


    }








    private void addCard(

            String title,

            String value,

            int column,

            int row

    ){


        VBox card =
                new VBox(5);



        card.getStyleClass()
                .add("kpi-card");



        Label titleLabel =
                new Label(title);



        titleLabel.getStyleClass()
                .add("kpi-title");



        Label valueLabel =
                new Label(value);



        valueLabel.getStyleClass()
                .add("kpi-value");



        card.getChildren()
                .addAll(
                        titleLabel,
                        valueLabel
                );



        kpiGrid.add(
                card,
                column,
                row
        );


    }









    // =====================================================
    // SALES CHART
    // =====================================================


    private void loadSalesChart(){


    if(chartContainer == null){

        return;

    }



    chartContainer.getChildren().clear();



    CategoryAxis xAxis =
            new CategoryAxis();


    NumberAxis yAxis =
            new NumberAxis();



    xAxis.setLabel("Date");

    yAxis.setLabel("Sales MK");



    LineChart<String,Number> chart =
            new LineChart<>(
                    xAxis,
                    yAxis
            );



    chart.setAnimated(false);

    chart.setLegendVisible(false);

    chart.setCreateSymbols(true);

    chart.setPrefHeight(260);

    chart.setPrefWidth(480);



    chart.setStyle(
            "-fx-background-color:white;"
    );



    XYChart.Series<String,Number> series =
            new XYChart.Series<>();


    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM"
            );



    Map<String,Double> sales =
            saleService.getSalesOverview();




    for(Map.Entry<String,Double> entry : sales.entrySet()){



        LocalDate date =
                LocalDate.parse(
                        entry.getKey()
                );



        series.getData()
                .add(

                    new XYChart.Data<>(

                        date.format(formatter),

                        entry.getValue()

                    )

                );


    }



    chart.getData()
            .add(series);



    chart.applyCss();



    // GREEN SALES LINE

    series.getNode()
            .setStyle(
              "-fx-stroke:#2E7D32;"
              +
              "-fx-stroke-width:3px;"
            );



    for(
        XYChart.Data<String,Number> data
        :
        series.getData()
    ){


        if(data.getNode()!=null){


            data.getNode()
                    .setStyle(

                    "-fx-background-color:#2E7D32,white;"

                    );

        }

    }



    chartContainer.getChildren()
            .add(chart);



}







    @FXML
    private void closePreview(){


        if(stage != null){

            stage.close();

        }


    }



}