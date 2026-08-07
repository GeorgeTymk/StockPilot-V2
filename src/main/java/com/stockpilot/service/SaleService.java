package com.stockpilot.service;


import com.stockpilot.database.Database;
import com.stockpilot.model.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class SaleService {



    private final InventoryHistoryService historyService =
            new InventoryHistoryService();




    public boolean saveSale(
            int recipeId,
            int quantity,
            double total
    ){


        String saleSQL =
                """
                INSERT INTO sales
                (
                    recipe_id,
                    quantity,
                    total
                )
                VALUES (?, ?, ?)
                """;



        String ingredientSQL =
                """
                SELECT
                    ingredient_id,
                    quantity_used
                FROM recipe_ingredients
                WHERE recipe_id = ?
                """;



        String updateSQL =
                """
                UPDATE ingredients
                SET quantity = quantity - ?
                WHERE id = ?
                """;



        try(
                Connection connection = Database.connect()
        ){


            connection.setAutoCommit(false);



            // SAVE SALE

            try(
                    PreparedStatement sale =
                            connection.prepareStatement(saleSQL)
            ){


                sale.setInt(1, recipeId);

                sale.setInt(2, quantity);

                sale.setDouble(3, total);


                sale.executeUpdate();


            }





            // REDUCE INGREDIENT STOCK

            try(
                    PreparedStatement ingredient =
                            connection.prepareStatement(ingredientSQL)
            ){


                ingredient.setInt(
                        1,
                        recipeId
                );



                try(
                        ResultSet rs =
                                ingredient.executeQuery()
                ){



                    while(rs.next()){


                        double reduction =
                                rs.getDouble(
                                        "quantity_used"
                                )
                                *
                                quantity;




                        int ingredientId =
                                rs.getInt(
                                        "ingredient_id"
                                );





                        try(
                                PreparedStatement update =
                                        connection.prepareStatement(updateSQL)
                        ){


                            update.setDouble(
                                    1,
                                    reduction
                            );


                            update.setInt(
                                    2,
                                    ingredientId
                            );


                            update.executeUpdate();


                        }





                        // RECORD INVENTORY HISTORY

                        historyService.recordMovement(

                                ingredientId,

                                "SALE",

                                reduction

                        );



                    }



                }


            }




            connection.commit();



            System.out.println(
                    "Sale completed and stock updated"
            );


            return true;



        }
        catch(Exception e){


            e.printStackTrace();


            return false;


        }


    }









    public List<Sale> getAllSales(){


        List<Sale> sales =
                new ArrayList<>();



        String sql =
                """
                SELECT
                    s.id,
                    s.recipe_id,
                    r.name AS recipe_name,
                    s.quantity,
                    s.total,
                    s.sale_date

                FROM sales s

                JOIN recipes r

                ON s.recipe_id = r.id

                ORDER BY s.sale_date DESC
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){



            while(result.next()){


                sales.add(

                        new Sale(

                                result.getInt("id"),

                                result.getInt("recipe_id"),

                                result.getString("recipe_name"),

                                result.getInt("quantity"),

                                result.getDouble("total"),

                                result.getString("sale_date")

                        )

                );


            }



        }
        catch(Exception e){

            e.printStackTrace();

        }



        return sales;


    }










    public double getTotalSales(){


        String sql =
                """
                SELECT IFNULL(SUM(total),0)
                FROM sales
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){


            if(result.next()){

                return result.getDouble(1);

            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return 0;


    }









    public int getTotalOrders(){


        String sql =
                """
                SELECT COUNT(*)
                FROM sales
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){


            if(result.next()){

                return result.getInt(1);

            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return 0;


    }









    public double getTodaySales(){


        String sql =
                """
                SELECT IFNULL(SUM(total),0)

                FROM sales

                WHERE DATE(sale_date)=DATE('now')
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){


            if(result.next()){

                return result.getDouble(1);

            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return 0;


    }









    public Map<String,Double> getSalesOverview(){


        Map<String,Double> sales =
                new LinkedHashMap<>();



        String sql =
                """
                SELECT

                    DATE(sale_date) AS day,

                    SUM(total) AS amount


                FROM sales


                GROUP BY DATE(sale_date)


                ORDER BY DATE(sale_date)


                LIMIT 7

                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){



            while(result.next()){


                sales.put(

                        result.getString("day"),

                        result.getDouble("amount")

                );


            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return sales;


    }









    public String getBestSellingRecipe(){


        String sql =
                """
                SELECT
                    r.name

                FROM sales s

                JOIN recipes r

                ON s.recipe_id = r.id

                GROUP BY r.name

                ORDER BY SUM(s.quantity) DESC

                LIMIT 1
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql);


                ResultSet result =
                        statement.executeQuery()

        ){


            if(result.next()){

                return result.getString("name");

            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return "No sales yet";


    }



}