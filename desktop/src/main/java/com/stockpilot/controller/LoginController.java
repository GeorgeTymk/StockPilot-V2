package com.stockpilot.controller;


import com.stockpilot.util.Navigator;
import com.stockpilot.service.AuthService;


import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;

import javafx.fxml.FXML;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import javafx.util.Duration;



public class LoginController {



    @FXML
    private StackPane root;



    @FXML
    private TextField usernameField;



    @FXML
    private PasswordField passwordField;



    private final AuthService authService = new AuthService();





    @FXML
    public void initialize() {


        if (root != null) {


            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(900),
                            root
                    );


            fade.setFromValue(0);

            fade.setToValue(1);

            fade.play();





            TranslateTransition slide =
                    new TranslateTransition(
                            Duration.millis(700),
                            root
                    );


            slide.setFromY(40);

            slide.setToY(0);

            slide.play();


        }


    }







    @FXML
    private void login() {



        String username =
                usernameField.getText();



        String password =
                passwordField.getText();





        boolean success =
                authService.authenticate(
                        username,
                        password
                );





        if (success) {


            System.out.println(
                    "Login successful"
            );



            /*
             * Load the main application shell.
             * The sidebar stays fixed here.
             * Pages load inside contentArea.
             */

            Navigator.goTo(
                    "shell/MainShell.fxml"
            );


        } 
        else {


            System.out.println(
                    "Invalid username or password"
            );


        }


    }







    @FXML
    private void goBack() {


        Navigator.goBack();


    }




}