package com.stockpilot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserSeeder {


    public static void createAdmin() {


        String sql = """
                INSERT OR IGNORE INTO users(username, password, role)
                VALUES (?, ?, ?)
                """;


        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, "admin");
            statement.setString(2, "admin123");
            statement.setString(3, "ADMIN");


            statement.executeUpdate();


            System.out.println("Admin user created");


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}