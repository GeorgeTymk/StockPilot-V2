package com.stockpilot;

import atlantafx.base.theme.PrimerLight;
import com.stockpilot.database.DatabaseInitializer;
import com.stockpilot.database.UserSeeder;
import com.stockpilot.util.Navigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize database on every startup
        DatabaseInitializer.createTables();
        UserSeeder.createAdmin();

        // Force-load FontAwesome
        FontIcon icon = new FontIcon(FontAwesomeSolid.CUBES);

        Navigator.setStage(stage);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(
                loader.load(),
                1280,
                720
        );

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        scene.getStylesheets().add(
                getClass()
                        .getResource("/css/stockpilot.css")
                        .toExternalForm()
        );

        stage.setTitle("StockPilot");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}