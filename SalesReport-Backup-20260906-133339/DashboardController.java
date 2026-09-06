package com.stockpilot.controller;


import com.stockpilot.model.Activity;
import com.stockpilot.service.ActivityService;
import com.stockpilot.service.DashboardService;
import com.stockpilot.service.IngredientService;
import com.stockpilot.service.SaleService;
import com.stockpilot.util.Navigator;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.chart.LineChart;

import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;


import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;



public class DashboardController {


    private final DashboardService dashboardService =
            new DashboardService();


    private final SaleService saleService =
            new SaleService();


    private final IngredientService ingredientService =
            new IngredientService();


    private final ActivityService activityService =
            new ActivityService();



    private final NumberFormat currencyFormat =
        java.text.NumberFormat
        .getCurrencyInstance(Locale.US);





    @FXML
    private Label totalSalesLabel;


    @FXML
    private Label totalOrdersLabel;


    @FXML
    private Label todaySalesLabel;


    @FXML
    private Label bestSellerLabel;


    @FXML
    private Label lowStockLabel;


    @FXML
    private Label totalIngredientsLabel;


    @FXML
    private Label outOfStockLabel;

    @FXML
    private ComboBox<String> totalSalesFilter;

    @FXML
    private Label totalSalesPeriodLabel;

    private java.time.LocalDate customFrom;

    private java.time.LocalDate customTo;




    @FXML
    private LineChart<String,Number> salesChart;



    @FXML
    private ListView<Activity> activityListView;







    @FXML
    public void initialize(){


        loadDashboardStatistics();


        loadSalesChart();


        loadRecentActivity();


    }









    // =====================================================
    // DASHBOARD NUMBERS
    // =====================================================


    private void loadDashboardStatistics(){



        double totalSales =
                saleService.getTotalSales();



        int totalOrders =
                saleService.getTotalOrders();



        double todaySales =
                saleService.getTodaySales();



        String bestSeller =
                saleService.getBestSellingRecipe();





        /*
         LOW STOCK:
         quantity is above zero
         but below minimum level
        */

        int lowStock =
                ingredientService.getLowStockCount();





        /*
         OUT OF STOCK:
         quantity is zero or below
        */

        int outOfStock =
                ingredientService.getOutOfStockCount();





        int totalIngredients =
                ingredientService.getIngredientCount();








        if(totalSalesLabel != null){

            totalSalesLabel.setText(
        "MK "
        + String.format(
                "%,.2f",
                totalSales
        )
);

        }






        if(totalOrdersLabel != null){

            totalOrdersLabel.setText(
                    String.valueOf(totalOrders)
            );

        }







        if(todaySalesLabel != null){

    todaySalesLabel.setText(
        "MK "
        + String.format(
                "%,.2f",
                totalSales
        )
);

        }






        if(bestSellerLabel != null){

            bestSellerLabel.setText(
        bestSeller == null ||
        bestSeller.isBlank()
        ?
        "No sales yet"
        :
        bestSeller
);

        }








        if(lowStockLabel != null){

            lowStockLabel.setText(
                    String.valueOf(lowStock)
            );

        }







        if(totalIngredientsLabel != null){

            totalIngredientsLabel.setText(
                    String.valueOf(totalIngredients)
            );

        }







        if(outOfStockLabel != null){

            outOfStockLabel.setText(
                    String.valueOf(outOfStock)
            );

        }




        System.out.println("----------------------------");
System.out.println("Low Stock Count  : " + lowStock);
System.out.println("Out of Stock Count : " + outOfStock);
System.out.println("----------------------------");


    }









    // =====================================================
    // RECENT ACTIVITY
    // =====================================================


    private void loadRecentActivity(){


        if(activityListView == null){

            return;

        }



        activityListView.setCellFactory(list ->
                new ListCell<Activity>(){


                    @Override
                    protected void updateItem(
                            Activity activity,
                            boolean empty
                    ){


                        super.updateItem(
                                activity,
                                empty
                        );


                        if(empty || activity == null){

                            setGraphic(null);
                            setText(null);

                            return;

                        }




                        Label message =
                                new Label(
                                        activity.getMessage()
                                );


                        Label type =
                                new Label(
                                        activity.getType()
                                                .toUpperCase()
                                );



                        Label time =
                                new Label(
                                        activity.getTime()
                                );



                        VBox box =
                                new VBox(
                                        6
                                );


                        box.getChildren()
                                .addAll(
                                        message,
                                        type,
                                        time
                                );



                        setGraphic(box);


                    }

                }
        );





        ObservableList<Activity> activities =

                FXCollections.observableArrayList(

                        activityService.getRecentActivities()

                );



        activityListView.setItems(
                activities
        );



    }









    // =====================================================
    // SALES CHART
    // =====================================================


    private void loadSalesChart(){


        if(salesChart == null){

            return;

        }



        XYChart.Series<String,Number> series =

                new XYChart.Series<>();


        series.setName("Sales");



        Map<String,Double> sales =
                saleService.getSalesOverview();




        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yy"
                );



        for(
                Map.Entry<String,Double> entry :
                sales.entrySet()
        ){


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




        salesChart.getData()
                .clear();



        salesChart.getData()
                .add(series);




    }










    // =====================================================
    // NAVIGATION
    // =====================================================


    @FXML
    private void openSellRecipe(){

        Navigator.goTo(
                "sell_recipe.fxml"
        );

    }



    @FXML
    private void openInventory(){

        Navigator.goTo(
                "inventory.fxml"
        );

    }



    @FXML
    private void openSales(){

        Navigator.goTo(
                "sales.fxml"
        );

    }



    @FXML
    private void openSalesHistory(){

        Navigator.goTo(
                "sales_history.fxml"
        );

    }



    @FXML
    private void openRecipes(){

        Navigator.goTo(
                "recipes.fxml"
        );

    }



    @FXML
    private void openSuppliers(){

        Navigator.goTo(
                "suppliers.fxml"
        );

    }



    @FXML
    private void openIngredients(){

        Navigator.goTo(
                "ingredients.fxml"
        );

    }



    @FXML
    private void openLowStock(){

        Navigator.goTo(
                "low_stock.fxml"
        );

    }



    @FXML
    private void openReports(){

        Navigator.goTo(
                "reports.fxml"
        );

    }



    @FXML
    private void logout(){

        Navigator.goTo(
                "login.fxml"
        );

    }




    // =====================================================
    // TOTAL SALES FILTER
    // =====================================================

    private void setupTotalSalesFilter(){

        if(totalSalesFilter == null){

            return;

        }

        totalSalesFilter.getItems().setAll(
                "All Time",
                "Today",
                "Last 7 Days",
                "Last 14 Days",
                "Last 30 Days",
                "This Month",
                "Custom Range..."
        );

        totalSalesFilter.setValue("All Time");

        totalSalesFilter.setOnAction(
                event -> updateFilteredTotalSales()
        );

        updateFilteredTotalSales();

    }


    private void updateFilteredTotalSales(){

        if(totalSalesFilter == null){

            return;

        }

        String selected =
                totalSalesFilter.getValue();

        java.time.LocalDate today =
                java.time.LocalDate.now();

        java.time.LocalDate from = null;

        java.time.LocalDate to = null;

        String label = "All Time";


        if("Today".equals(selected)){

            from = today;

            to = today;

            label = "Today";

        }
        else if("Last 7 Days".equals(selected)){

            from = today.minusDays(6);

            to = today;

            label = "Last 7 Days";

        }
        else if("Last 14 Days".equals(selected)){

            from = today.minusDays(13);

            to = today;

            label = "Last 14 Days";

        }
        else if("Last 30 Days".equals(selected)){

            from = today.minusDays(29);

            to = today;

            label = "Last 30 Days";

        }
        else if("This Month".equals(selected)){

            from = today.withDayOfMonth(1);

            to = today;

            label = "This Month";

        }
        else if("Custom Range...".equals(selected)){

            if(!showCustomSalesRange()){

                totalSalesFilter.setValue("All Time");

                return;

            }

            from = customFrom;

            to = customTo;

            label =
                    customFrom
                            + " to "
                            + customTo;

        }


        double value =
                saleService.getSalesBetween(
                        from,
                        to
                );


        if("All Time".equals(selected)){

            value =
                    saleService.getTotalSales();

        }


        if(totalSalesLabel != null){

            totalSalesLabel.setText(
                    "MK "
                    + String.format(
                            Locale.US,
                            "%,.2f",
                            value
                    )
            );

        }


        if(totalSalesPeriodLabel != null){

            totalSalesPeriodLabel.setText(
                    label
            );

        }

    }


    private boolean showCustomSalesRange(){

        DatePicker fromPicker =
                new DatePicker(
                        customFrom != null
                                ? customFrom
                                : java.time.LocalDate.now()
                                .minusDays(29)
                );


        DatePicker toPicker =
                new DatePicker(
                        customTo != null
                                ? customTo
                                : java.time.LocalDate.now()
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


        Dialog<Pair<java.time.LocalDate,
                java.time.LocalDate>> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Custom Sales Range"
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


        dialog.setResultConverter(
                button -> {

                    if(button == apply){

                        return new Pair<>(
                                fromPicker.getValue(),
                                toPicker.getValue()
                        );

                    }

                    return null;

                }
        );


        java.util.Optional<
                Pair<java.time.LocalDate,
                     java.time.LocalDate>>
                result =
                dialog.showAndWait();


        if(result.isEmpty()){

            return false;

        }


        java.time.LocalDate from =
                result.get().getKey();

        java.time.LocalDate to =
                result.get().getValue();


        if(from == null
                || to == null
                || from.isAfter(to)){

            return false;

        }


        customFrom = from;

        customTo = to;

        return true;

    }


    @FXML
    private void openOutOfStock(){

        LowStockController.setOutOfStockOnly(true);

        Navigator.goTo(
                "low_stock.fxml"
        );

    }

    @FXML
    private void openTotalSalesFilter(){

        LocalDate today =
                LocalDate.now();

        javafx.scene.control.ComboBox<String> period =
                new javafx.scene.control.ComboBox<>();

        period.getItems().setAll(
                "All Time",
                "Today",
                "Last 7 Days",
                "Last 14 Days",
                "Last 30 Days",
                "This Month",
                "Custom Range"
        );

        period.setValue("All Time");

        DatePicker fromPicker =
                new DatePicker(
                        today.minusDays(29)
                );

        DatePicker toPicker =
                new DatePicker(
                        today
                );

        GridPane grid =
                new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);

        grid.add(
                new Label("Period"),
                0,
                0
        );

        grid.add(
                period,
                1,
                0
        );

        grid.add(
                new Label("From"),
                0,
                1
        );

        grid.add(
                fromPicker,
                1,
                1
        );

        grid.add(
                new Label("To"),
                0,
                2
        );

        grid.add(
                toPicker,
                1,
                2
        );

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Total Sales"
        );

        dialog.setHeaderText(
                "Filter Total Sales"
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

        dialog.showAndWait()
                .ifPresent(result -> {

                    if(result != apply){
                        return;
                    }

                    LocalDate from = null;
                    LocalDate to = null;

                    String selected =
                            period.getValue();

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

                        from =
                                fromPicker.getValue();

                        to =
                                toPicker.getValue();

                        if(
                                from == null
                                || to == null
                                || from.isAfter(to)
                        ){
                            return;
                        }

                    }

                    double amount;

                    if("All Time".equals(selected)){

                        amount =
                                saleService.getTotalSales();

                    }
                    else{

                        amount =
                                saleService.getSalesBetween(
                                        from,
                                        to
                                );

                    }

                    if(totalSalesLabel != null){

                        totalSalesLabel.setText(
                                "MK "
                                + String.format(
                                        Locale.US,
                                        "%,.2f",
                                        amount
                                )
                        );

                    }

                });

    }

    @FXML
    private void openOrdersFilter(){

        LocalDate today =
                LocalDate.now();

        javafx.scene.control.ComboBox<String> period =
                new javafx.scene.control.ComboBox<>();

        period.getItems().setAll(
                "All Time",
                "Today",
                "Last 7 Days",
                "Last 14 Days",
                "Last 30 Days",
                "This Month",
                "Custom Range"
        );

        period.setValue("All Time");

        DatePicker fromPicker =
                new DatePicker(today.minusDays(29));

        DatePicker toPicker =
                new DatePicker(today);

        GridPane grid =
                new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);

        grid.add(new Label("Period"), 0, 0);
        grid.add(period, 1, 0);

        grid.add(new Label("From"), 0, 1);
        grid.add(fromPicker, 1, 1);

        grid.add(new Label("To"), 0, 2);
        grid.add(toPicker, 1, 2);

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle("Orders");
        dialog.setHeaderText("Filter Orders");

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

        dialog.showAndWait().ifPresent(result -> {

            if(result != apply){
                return;
            }

            String selected =
                    period.getValue();

            LocalDate from = null;
            LocalDate to = null;

            if("Today".equals(selected)){

                from = today;
                to = today;

            }
            else if("Last 7 Days".equals(selected)){

                from = today.minusDays(6);
                to = today;

            }
            else if("Last 14 Days".equals(selected)){

                from = today.minusDays(13);
                to = today;

            }
            else if("Last 30 Days".equals(selected)){

                from = today.minusDays(29);
                to = today;

            }
            else if("This Month".equals(selected)){

                from = today.withDayOfMonth(1);
                to = today;

            }
            else if("Custom Range".equals(selected)){

                from = fromPicker.getValue();
                to = toPicker.getValue();

                if(
                        from == null
                        || to == null
                        || from.isAfter(to)
                ){

                    return;

                }

            }

            int orders;

            if("All Time".equals(selected)){

                orders =
                        saleService.getTotalOrders();

            }
            else{

                orders =
                        saleService.getOrdersBetween(
                                from,
                                to
                        );

            }

            if(totalOrdersLabel != null){

                totalOrdersLabel.setText(
                        String.valueOf(orders)
                );

            }

        });

    }
}
