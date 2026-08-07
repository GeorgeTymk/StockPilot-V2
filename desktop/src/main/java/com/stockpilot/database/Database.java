package com.stockpilot.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String APP_FOLDER =
            System.getProperty("user.home")
                    + File.separator
                    + "AppData"
                    + File.separator
                    + "Local"
                    + File.separator
                    + "StockPilot";

    private static final String DB_PATH =
            APP_FOLDER
                    + File.separator
                    + "stockpilot.db";

    private static final String URL =
            "jdbc:sqlite:" + DB_PATH;

    public static Connection connect() throws SQLException {

        File folder = new File(APP_FOLDER);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        Connection connection =
                DriverManager.getConnection(URL);

        Statement statement =
                connection.createStatement();

        statement.execute("PRAGMA busy_timeout=5000");

        statement.close();

        System.out.println("Database:");
        System.out.println(DB_PATH);

        return connection;
    }
}