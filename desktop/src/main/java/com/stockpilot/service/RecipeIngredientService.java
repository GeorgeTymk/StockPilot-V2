package com.stockpilot.service;

import com.stockpilot.database.Database;
import com.stockpilot.model.RecipeIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientService {

    public void addIngredientToRecipe(
            int recipeId,
            int ingredientId,
            double quantity
    ) {

        String sql =
                """
                INSERT INTO recipe_ingredients
                (recipe_id, ingredient_id, quantity_used)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recipeId
            );

            statement.setInt(
                    2,
                    ingredientId
            );

            statement.setDouble(
                    3,
                    quantity
            );

            int rows =
                    statement.executeUpdate();

            System.out.println(
                    "Ingredient added to recipe. Rows affected: "
                            + rows
            );

        } catch (Exception e) {

            System.out.println(
                    "Error adding ingredient to recipe."
            );

            e.printStackTrace();

        }

    }

    public List<RecipeIngredient> getRecipeIngredients(
            int recipeId
    ) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        String sql =
                """
                SELECT
                    ri.id,
                    ri.recipe_id,
                    i.id AS ingredient_id,
                    i.name,
                    ri.quantity_used,
                    i.unit

                FROM recipe_ingredients ri

                JOIN ingredients i

                ON ri.ingredient_id = i.id

                WHERE ri.recipe_id = ?
                """;

        try (
                Connection connection = Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    recipeId
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                ingredients.add(

                        new RecipeIngredient(

                                result.getInt("id"),

                                result.getInt("recipe_id"),

                                result.getInt("ingredient_id"),

                                result.getString("name"),

                                result.getDouble("quantity_used"),

                                result.getString("unit")

                        )

                );

            }

            System.out.println(
                    "Recipe ingredients loaded: "
                            + ingredients.size()
            );

        } catch (Exception e) {

            System.out.println(
                    "Error loading recipe ingredients."
            );

            e.printStackTrace();

        }

        return ingredients;

    }

public void reduceIngredientsForSale(
        int recipeId,
        int soldQuantity
) {


    String sql =
            """
            SELECT
                ingredient_id,
                quantity_used

            FROM recipe_ingredients

            WHERE recipe_id = ?
            """;



    try(
            Connection connection = Database.connect();

            PreparedStatement statement =
                    connection.prepareStatement(sql)

    ){


        statement.setInt(
                1,
                recipeId
        );


        ResultSet result =
                statement.executeQuery();



        while(result.next()){


            int ingredientId =
                    result.getInt(
                            "ingredient_id"
                    );


            double amountUsed =
                    result.getDouble(
                            "quantity_used"
                    );



            double totalReduction =
                    amountUsed * soldQuantity;



            updateIngredientStock(

                    ingredientId,

                    totalReduction

            );


        }



    }catch(Exception e){

        e.printStackTrace();

    }


}







private void updateIngredientStock(
        int ingredientId,
        double amount
){


    String sql =
            """
            UPDATE ingredients

            SET quantity = quantity - ?

            WHERE id = ?
            """;



    try(

            Connection connection =
                    Database.connect();

            PreparedStatement statement =
                    connection.prepareStatement(sql)

    ){


        statement.setDouble(
                1,
                amount
        );


        statement.setInt(
                2,
                ingredientId
        );


        statement.executeUpdate();



    }catch(Exception e){

        e.printStackTrace();

    }


}

    public void removeIngredientFromRecipe(
            int id
    ) {

        String sql =
                """
                DELETE FROM recipe_ingredients
                WHERE id = ?
                """;

        try (
                Connection connection = Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    id
            );

            int rows =
                    statement.executeUpdate();

            System.out.println(
                    "Ingredient removed. Rows affected: "
                            + rows
            );

        } catch (Exception e) {

            System.out.println(
                    "Error removing ingredient."
            );

            e.printStackTrace();

        }

    }

    // =====================================================
    // Returns every ingredient used by a specific recipe.
    // Used when deducting inventory after a sale.
    // =====================================================

    public List<RecipeIngredient> getIngredientsForRecipe(
            int recipeId
    ) {

        return getRecipeIngredients(recipeId);

    }

}