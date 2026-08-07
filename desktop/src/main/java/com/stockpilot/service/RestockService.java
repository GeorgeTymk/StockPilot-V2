package com.stockpilot.service;


import com.stockpilot.database.Database;


import java.sql.Connection;
import java.sql.PreparedStatement;



public class RestockService {



    private final InventoryHistoryService historyService =
            new InventoryHistoryService();





    public boolean restock(

            int supplierId,

            int ingredientId,

            double quantity,

            double cost

    ){


        String purchaseSQL =
                """
                INSERT INTO purchases
                (
                    supplier_id,
                    total_cost
                )

                VALUES (?,?)
                """;



        String itemSQL =
                """
                INSERT INTO purchase_items
                (
                    purchase_id,
                    ingredient_id,
                    quantity,
                    cost
                )

                VALUES (?,?,?,?)
                """;



        String stockSQL =
                """
                UPDATE ingredients

                SET quantity = quantity + ?

                WHERE id = ?
                """;





        try(

            Connection connection =
                    Database.connect()

        ){



            connection.setAutoCommit(false);




            int purchaseId = 0;



            // SAVE PURCHASE

            try(
                PreparedStatement statement =
                    connection.prepareStatement(
                            purchaseSQL,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    )
            ){


                statement.setInt(
                        1,
                        supplierId
                );


                statement.setDouble(
                        2,
                        cost
                );


                statement.executeUpdate();



                var keys =
                        statement.getGeneratedKeys();


                if(keys.next()){

                    purchaseId =
                            keys.getInt(1);

                }


            }






            // SAVE PURCHASE ITEM


            try(
                PreparedStatement statement =
                        connection.prepareStatement(itemSQL)

            ){


                statement.setInt(
                        1,
                        purchaseId
                );


                statement.setInt(
                        2,
                        ingredientId
                );


                statement.setDouble(
                        3,
                        quantity
                );


                statement.setDouble(
                        4,
                        cost
                );


                statement.executeUpdate();


            }







            // UPDATE STOCK


            try(
                PreparedStatement statement =
                        connection.prepareStatement(stockSQL)

            ){


                statement.setDouble(
                        1,
                        quantity
                );


                statement.setInt(
                        2,
                        ingredientId
                );


                statement.executeUpdate();


            }






            // RECORD HISTORY

            historyService.recordMovement(

                    ingredientId,

                    "RESTOCK",

                    quantity

            );






            connection.commit();



            System.out.println(
                    "Restock completed"
            );



            return true;


        }

        catch(Exception e){


            e.printStackTrace();

            return false;

        }


    }


}