package com.stockpilot.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;


import com.stockpilot.service.PDFReportService;
import com.stockpilot.service.SaleService;
import com.stockpilot.service.IngredientService;

import com.stockpilot.model.Ingredient;
import com.stockpilot.util.Navigator;


import java.io.IOException;



public class ReportsController {


    @FXML
    private CheckBox salesSummaryCheck;

    @FXML
    private CheckBox todaySalesCheck;

    @FXML
    private CheckBox inventoryCheck;

    @FXML
    private CheckBox lowStockCheck;

    @FXML
    private CheckBox outOfStockCheck;

    @FXML
    private CheckBox recipeCheck;

    @FXML
    private CheckBox activityCheck;

    @FXML
    private CheckBox bestSellerCheck;

    @FXML
    private TextArea previewArea;



    private String generatedReport;



    private final SaleService saleService =
            new SaleService();


    private final IngredientService ingredientService =
            new IngredientService();


    private final PDFReportService pdfReportService =
            new PDFReportService();





    @FXML
    public void initialize(){

        salesSummaryCheck.setSelected(true);

        todaySalesCheck.setSelected(true);

        bestSellerCheck.setSelected(true);

    }







    @FXML
    private void generatePreview(){


        StringBuilder report =
                new StringBuilder();



        report.append(
                "STOCKPILOT BUSINESS REPORT\n"
        );


        report.append(
                "====================================\n\n"
        );




        if(salesSummaryCheck.isSelected()){


            report.append("SALES SUMMARY\n");

            report.append("------------------------------------\n");


            report.append(
                    "Total Revenue: MK "
            );


            report.append(
                    String.format(
                            "%,.2f",
                            saleService.getTotalSales()
                    )
            );


            report.append("\n");


            report.append(
                    "Total Orders: "
            );


            report.append(
                    saleService.getTotalOrders()
            );


            report.append("\n\n");

        }







        if(todaySalesCheck.isSelected()){


            report.append(
                    "TODAY'S PERFORMANCE\n"
            );


            report.append(
                    "------------------------------------\n"
            );


            report.append(
                    "Today's Sales: MK "
            );


            report.append(
                    String.format(
                            "%,.2f",
                            saleService.getTodaySales()
                    )
            );


            report.append("\n\n");

        }








        if(bestSellerCheck.isSelected()){


            report.append(
                    "BEST SELLING RECIPE\n"
            );


            report.append(
                    "------------------------------------\n"
            );


            String bestSeller =
                    saleService.getBestSellingRecipe();



            if(bestSeller == null ||
                    bestSeller.isBlank()){


                report.append(
                        "No sales recorded"
                );


            }
            else{


                report.append(bestSeller);

            }


            report.append("\n\n");

        }








        if(inventoryCheck.isSelected()){


            report.append(
                    "INVENTORY STATUS\n"
            );


            report.append(
                    "------------------------------------\n"
            );


            report.append(
                    "Total Ingredients: "
            );


            report.append(
                    ingredientService.getIngredientCount()
            );


            report.append("\n\n");

        }








        if(lowStockCheck.isSelected()){


            report.append(
                    "LOW STOCK ITEMS\n"
            );


            report.append(
                    "------------------------------------\n"
            );



            var items =
                    ingredientService.getLowStockIngredients();



            if(items.isEmpty()){


                report.append(
                        "No low stock items\n"
                );


            }
            else{


                for(Ingredient ingredient : items){


                    report.append(
                            ingredient.getName()
                            +
                            " - "
                            +
                            ingredient.getQuantity()
                            +
                            " "
                            +
                            ingredient.getUnit()
                            +
                            "\n"
                    );


                }


            }


            report.append("\n");

        }








        if(outOfStockCheck.isSelected()){


            report.append(
                    "OUT OF STOCK ITEMS\n"
            );


            report.append(
                    "------------------------------------\n"
            );


            var items =
                    ingredientService.getOutOfStockIngredients();



            if(items.isEmpty()){


                report.append(
                        "No out of stock items\n"
                );


            }
            else{


                for(Ingredient ingredient : items){


                    report.append(
                            ingredient.getName()
                            +
                            "\n"
                    );


                }


            }


            report.append("\n");

        }







        if(recipeCheck.isSelected()){


            report.append(
                    "✓ Recipe Performance Included\n"
            );

        }



        if(activityCheck.isSelected()){


            report.append(
                    "✓ Recent Activity Included\n"
            );

        }






        generatedReport =
                report.toString();



        showPreview(
                generatedReport
        );


    }









    @FXML
    private void exportPDF(){


        if(generatedReport == null ||
                generatedReport.isBlank()){


            Alert alert =
                    new Alert(
                            Alert.AlertType.WARNING
                    );


            alert.setTitle(
                    "No Report Available"
            );


            alert.setHeaderText(null);


            alert.setContentText(
                    "Generate the report preview first."
            );


            alert.showAndWait();


            return;

        }






        pdfReportService.generateReport(

        generatedReport,

        saleService.getTotalSales(),

        saleService.getTotalOrders(),

        ingredientService.getLowStockCount(),

        ingredientService.getOutOfStockCount()

);






        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "Export Complete"
        );


        alert.setHeaderText(null);


        alert.setContentText(
                "StockPilot PDF saved on Desktop."
        );


        alert.showAndWait();



    }










    private void showPreview(String report){


        try{


            FXMLLoader loader =
                    new FXMLLoader(

                            getClass()
                            .getResource(
                                    "/fxml/report_preview.fxml"
                            )

                    );



            Parent root =
                    loader.load();



            ReportPreviewController controller =
                    loader.getController();



            Stage stage =
                    new Stage();



            controller.setStage(stage);



            controller.setReport(report);




            Scene scene =
                    new Scene(root);



            scene.getStylesheets()
                    .add(

                            getClass()
                            .getResource(
                                    "/css/report-preview.css"
                            )
                            .toExternalForm()

                    );



            stage.setScene(scene);



            stage.initModality(
                    Modality.APPLICATION_MODAL
            );



            stage.setResizable(false);



            stage.setTitle(
                    "Report Preview"
            );



            stage.centerOnScreen();



            stage.showAndWait();


        }
        catch(IOException e){

            e.printStackTrace();

        }


    }








    @FXML
    private void goBack(){


        Navigator.goBack();


    }


}