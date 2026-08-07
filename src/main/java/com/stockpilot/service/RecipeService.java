package com.stockpilot.service;


import com.stockpilot.database.Database;
import com.stockpilot.model.Recipe;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



public class RecipeService {



    // Get all recipes
    public List<Recipe> getAllRecipes(){


        List<Recipe> recipes = new ArrayList<>();


        String sql =
                """
                SELECT *
                FROM recipes
                ORDER BY id DESC
                """;



        try(

                Connection connection = Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()

        ){



            while(result.next()){


                Recipe recipe =
                        new Recipe(

                                result.getInt("id"),

                                result.getString("name"),

                                result.getString("description"),

                                result.getDouble("selling_price")

                        );


                recipes.add(recipe);


            }



            System.out.println(
                    "Recipes loaded: "
                    + recipes.size()
            );



        }catch(Exception e){


            System.out.println(
                    "Error loading recipes"
            );


            e.printStackTrace();


        }



        return recipes;


    }





    // Add recipe
    public void addRecipe(Recipe recipe){



        String sql =
                """
                INSERT INTO recipes
                (
                    name,
                    description,
                    selling_price
                )

                VALUES
                (
                    ?,
                    ?,
                    ?
                )
                """;



        try(

                Connection connection =
                        Database.connect();


                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ){



            statement.setString(
                    1,
                    recipe.getName()
            );


            statement.setString(
                    2,
                    recipe.getDescription()
            );


            statement.setDouble(
                    3,
                    recipe.getSellingPrice()
            );



            statement.executeUpdate();



            System.out.println(
                    "Recipe added successfully"
            );



        }catch(Exception e){


            System.out.println(
                    "Error adding recipe"
            );


            e.printStackTrace();


        }


    }





    // Get recipe by ID
    public Recipe getRecipeById(int id){



        String sql =
                """
                SELECT *
                FROM recipes
                WHERE id = ?
                """;



        try(

                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ){



            statement.setInt(
                    1,
                    id
            );


            ResultSet result =
                    statement.executeQuery();



            if(result.next()){


                return new Recipe(

                        result.getInt("id"),

                        result.getString("name"),

                        result.getString("description"),

                        result.getDouble("selling_price")

                );


            }



        }catch(Exception e){


            e.printStackTrace();


        }



        return null;


    }





    // Delete recipe
    public void deleteRecipe(int id){



        String sql =
                """
                DELETE FROM recipes
                WHERE id = ?
                """;



        try(

                Connection connection =
                        Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql)

        ){



            statement.setInt(
                    1,
                    id
            );


            statement.executeUpdate();



            System.out.println(
                    "Recipe deleted"
            );



        }catch(Exception e){


            e.printStackTrace();


        }



    }



}