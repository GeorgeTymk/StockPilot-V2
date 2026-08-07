package com.stockpilot.service;


import com.stockpilot.database.Database;
import com.stockpilot.model.InventoryHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



public class InventoryHistoryService {



    public void recordMovement(

            int ingredientId,

            String movementType,

            double quantity

    ){


        String sql =
                """
                INSERT INTO inventory_history
                (
                    ingredient_id,
                    movement_type,
                    quantity
                )

                VALUES (?, ?, ?)
                """;


        try(

                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ){


            statement.setInt(
                    1,
                    ingredientId
            );


            statement.setString(
                    2,
                    movementType
            );


            statement.setDouble(
                    3,
                    quantity
            );


            statement.executeUpdate();


        }
        catch(Exception e){

            e.printStackTrace();

        }


    }




public List<InventoryHistory> getRecentHistory() {

    List<InventoryHistory> history =
            new ArrayList<>();

    String sql =
            """
            SELECT

                inventory_history.id,

                inventory_history.ingredient_id,

                ingredients.name,

                inventory_history.movement_type,

                inventory_history.quantity,

                inventory_history.created_at

            FROM inventory_history

            JOIN ingredients
              ON ingredients.id =
                 inventory_history.ingredient_id

            ORDER BY inventory_history.created_at DESC

            LIMIT 5
            """;

    try (

            Connection connection =
                    Database.connect();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery()

    ) {

        while(result.next()){

            history.add(

                    new InventoryHistory(

                            result.getInt("id"),

                            result.getInt("ingredient_id"),

                            result.getString("name"),

                            result.getString("movement_type"),

                            result.getDouble("quantity"),

                            result.getString("created_at")

                    )

            );

        }

    }
    catch(Exception e){

        e.printStackTrace();

    }

    return history;

}



    public List<InventoryHistory> getHistory(){


        List<InventoryHistory> history =
                new ArrayList<>();



        String sql =
                """
                SELECT

                    inventory_history.id,

                    inventory_history.ingredient_id,

                    ingredients.name,

                    inventory_history.movement_type,

                    inventory_history.quantity,

                    inventory_history.created_at


                FROM inventory_history


                LEFT JOIN ingredients

                ON inventory_history.ingredient_id =
                   ingredients.id


                ORDER BY inventory_history.created_at DESC

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


                history.add(

                        new InventoryHistory(

                                result.getInt("id"),

                                result.getInt("ingredient_id"),

                                result.getString("name"),

                                result.getString("movement_type"),

                                result.getDouble("quantity"),

                                result.getString("created_at")

                        )

                );


            }


        }
        catch(Exception e){

            e.printStackTrace();

        }



        return history;


    }



}