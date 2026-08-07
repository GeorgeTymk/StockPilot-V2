package com.stockpilot.service;

import com.stockpilot.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ReportService {


    public int getTotalIngredients() {

        String sql =
                """
                SELECT COUNT(*)
                FROM ingredients
                """;


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()) {


            if(result.next()) {
                return result.getInt(1);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }


        return 0;
    }





    public int getLowStockCount() {


        String sql =
                """
                SELECT COUNT(*)
                FROM ingredients
                WHERE quantity <= minimum_stock
                AND quantity > 0
                """;


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()) {


            if(result.next()) {
                return result.getInt(1);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }


        return 0;

    }







    public int getOutOfStockCount() {


        String sql =
                """
                SELECT COUNT(*)
                FROM ingredients
                WHERE quantity <= 0
                """;


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()) {


            if(result.next()) {
                return result.getInt(1);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }


        return 0;

    }



}