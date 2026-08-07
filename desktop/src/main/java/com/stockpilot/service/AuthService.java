package com.stockpilot.service;

import com.stockpilot.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {


    public boolean authenticate(String username, String password) {


        String sql = """
                SELECT * FROM users
                WHERE username = ?
                AND password = ?
                """;


        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, username);
            statement.setString(2, password);


            ResultSet result = statement.executeQuery();


            return result.next();


        } catch (Exception e) {

            e.printStackTrace();

        }


        return false;

    }

}