package com.stockpilot.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.InputStream;
import java.util.Properties;


public class AboutController {


    @FXML
    private Label versionLabel;


    @FXML
    private Label buildLabel;



    @FXML
    public void initialize() {


        try {

            Properties properties = new Properties();


            InputStream input =
                    getClass()
                    .getResourceAsStream("/version.properties");


            properties.load(input);


            versionLabel.setText(
                    "Version "
                    + properties.getProperty("app.version")
            );


            buildLabel.setText(
                    "Build "
                    + properties.getProperty("app.build")
            );


        } catch (Exception e) {


            versionLabel.setText(
                    "Version unavailable"
            );


            buildLabel.setText("");

        }

    }

}