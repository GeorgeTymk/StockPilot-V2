package com.stockpilot.service;


import com.stockpilot.database.Database;
import com.stockpilot.model.Supplier;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class SupplierService {



    public List<Supplier> getAllSuppliers(){


        List<Supplier> suppliers = new ArrayList<>();


        String sql = "SELECT * FROM suppliers";



        try(Connection connection = Database.connect();

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery(sql)){



            while(result.next()){


                suppliers.add(

                    new Supplier(

                        result.getInt("id"),

                        result.getString("name"),

                        result.getString("phone"),

                        result.getString("email")

                    )

                );


            }



        }catch(Exception e){

            e.printStackTrace();

        }



        return suppliers;


    }




    public void addSupplier(Supplier supplier){



        String sql =
        """
        INSERT INTO suppliers(name,phone,email)
        VALUES(?,?,?)
        """;



        try(Connection connection = Database.connect();

            PreparedStatement ps =
                    connection.prepareStatement(sql)){



            ps.setString(1,supplier.getName());

            ps.setString(2,supplier.getPhone());

            ps.setString(3,supplier.getEmail());



            ps.executeUpdate();



        }catch(Exception e){

            e.printStackTrace();

        }


    }


}