package com.stockpilot.service;


import com.stockpilot.database.Database;
import com.stockpilot.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class InventoryService {


    public List<Product> getAllProducts() {


        List<Product> products = new ArrayList<>();


        String sql = "SELECT * FROM products";


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()) {


            while(result.next()) {


                Product product = new Product(

                        result.getInt("id"),

                        result.getString("name"),

                        result.getString("category"),

                        result.getInt("quantity"),

                        result.getDouble("price")

                );


                products.add(product);

            }


        } catch(Exception e) {

            e.printStackTrace();

        }


        return products;

    }



    public void addProduct(Product product) {


        String sql =
                """
                INSERT INTO products(name, category, quantity, price)
                VALUES(?,?,?,?)
                """;


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setString(1, product.getName());

            statement.setString(2, product.getCategory());

            statement.setInt(3, product.getQuantity());

            statement.setDouble(4, product.getPrice());


            statement.executeUpdate();


        } catch(Exception e) {

            e.printStackTrace();

        }

    }



    public void deleteProduct(int id) {


        String sql = "DELETE FROM products WHERE id=?";


        try(Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {


            statement.setInt(1,id);

            statement.executeUpdate();


        } catch(Exception e) {

            e.printStackTrace();

        }


    }


}