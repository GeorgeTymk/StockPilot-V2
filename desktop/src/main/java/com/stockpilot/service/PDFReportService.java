package com.stockpilot.service;


import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.layout.properties.TextAlignment;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class PDFReportService {


    public void generateReport(

            String report,
            double revenue,
            int orders,
            int lowStock,
            int outStock

    ) {


        try {


            String filePath =
                    System.getProperty("user.home")
                    +
                    "/Desktop/StockPilot_Report.pdf";



            PdfWriter writer =
                    new PdfWriter(filePath);



            PdfDocument pdf =
                    new PdfDocument(writer);



            Document document =
                    new Document(
                            pdf,
                            PageSize.A4
                    );



            document.setMargins(
                    50,
                    50,
                    50,
                    50
            );




            // ================================
            // HEADER
            // ================================


            Paragraph title =
                    new Paragraph(
                            "STOCKPILOT"
                    );


            title.setBold();

            title.setFontSize(30);

            title.setFontColor(
                    ColorConstants.DARK_GRAY
            );


            title.setTextAlignment(
                    TextAlignment.CENTER
            );


            document.add(title);





            Paragraph system =
                    new Paragraph(
                            "Restaurant Inventory Management System"
                    );


            system.setFontSize(12);


            system.setTextAlignment(
                    TextAlignment.CENTER
            );


            document.add(system);





            Paragraph reportTitle =
                    new Paragraph(
                            "Business Performance Report"
                    );


            reportTitle.setBold();

            reportTitle.setFontSize(18);


            reportTitle.setTextAlignment(
                    TextAlignment.CENTER
            );


            document.add(reportTitle);





            Paragraph date =
                    new Paragraph(

                            "Generated: "
                            +
                            LocalDateTime.now()
                            .format(

                                    DateTimeFormatter.ofPattern(
                                            "dd MMMM yyyy HH:mm"
                                    )

                            )

                    );


            date.setFontSize(10);


            date.setTextAlignment(
                    TextAlignment.CENTER
            );


            document.add(date);




            document.add(
                    new Paragraph("")
            );


            document.add(
                    new LineSeparator(
                            new SolidLine()
                    )
            );





            // ================================
            // KPI SECTION
            // ================================


            Paragraph kpiTitle =
                    new Paragraph(
                            "KEY PERFORMANCE INDICATORS"
                    );


            kpiTitle.setBold();

            kpiTitle.setFontSize(16);


            document.add(kpiTitle);





            document.add(
                    new Paragraph(
                            "Total Revenue: MK "
                            +
                            String.format(
                                    "%,.2f",
                                    revenue
                            )
                    )
            );





            document.add(
                    new Paragraph(
                            "Total Orders: "
                            +
                            orders
                    )
            );





            document.add(
                    new Paragraph(
                            "Low Stock Items: "
                            +
                            lowStock
                    )
            );





            document.add(
                    new Paragraph(
                            "Out Of Stock Items: "
                            +
                            outStock
                    )
            );





            document.add(
                    new Paragraph("")
            );





            document.add(
                    new LineSeparator(
                            new SolidLine()
                    )
            );





            // ================================
            // REPORT CONTENT
            // ================================


            Paragraph contentTitle =
                    new Paragraph(
                            "REPORT DETAILS"
                    );


            contentTitle.setBold();

            contentTitle.setFontSize(16);


            document.add(contentTitle);





            String[] lines =
                    report.split("\n");



            for(String line : lines){


                Paragraph paragraph =
                        new Paragraph(
                                line
                        );


                paragraph.setFontSize(11);


                document.add(
                        paragraph
                );


            }







            // ================================
            // FOOTER
            // ================================


            document.add(
                    new Paragraph("")
            );


            document.add(
                    new LineSeparator(
                            new SolidLine()
                    )
            );




            Paragraph footer =
                    new Paragraph(

                            "Generated by StockPilot Restaurant Inventory System\n"
                            +
                            "Confidential Business Report"

                    );


            footer.setFontSize(9);


            footer.setTextAlignment(
                    TextAlignment.CENTER
            );


            document.add(footer);





            document.close();




            System.out.println(

                    "PDF Created Successfully: "
                    +
                    filePath

            );



        }
        catch(Exception e){


            e.printStackTrace();


        }


    }


}