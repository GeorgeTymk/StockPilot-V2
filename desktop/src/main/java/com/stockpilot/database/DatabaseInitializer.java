package com.stockpilot.database;


import java.sql.Connection;
import java.sql.Statement;


public class DatabaseInitializer {


    public static void createTables() {


        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    username TEXT NOT NULL UNIQUE,

                    password TEXT NOT NULL,

                    role TEXT NOT NULL

                );
                """;



        String productsTable = """
                CREATE TABLE IF NOT EXISTS products (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    name TEXT NOT NULL,

                    category TEXT,

                    quantity REAL NOT NULL,

                    price REAL NOT NULL,

                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP

                );
                """;



        String ingredientsTable = """
                CREATE TABLE IF NOT EXISTS ingredients (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    name TEXT NOT NULL UNIQUE,

                    quantity REAL NOT NULL,

                    unit TEXT NOT NULL,

                    minimum_stock REAL NOT NULL DEFAULT 0,

                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP

                );
                """;



        String inventoryHistoryTable = """
                CREATE TABLE IF NOT EXISTS inventory_history (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    ingredient_id INTEGER NOT NULL,

                    movement_type TEXT NOT NULL,

                    quantity REAL NOT NULL,

                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                    FOREIGN KEY (ingredient_id)
                    REFERENCES ingredients(id)

                );
                """;



        String activitiesTable = """
                CREATE TABLE IF NOT EXISTS activities (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    message TEXT,

                    type TEXT,

                    activity_time DATETIME DEFAULT CURRENT_TIMESTAMP

                );
                """;



        String suppliersTable = """
                CREATE TABLE IF NOT EXISTS suppliers (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    name TEXT NOT NULL,

                    phone TEXT,

                    email TEXT,

                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP

                );
                """;



        // CONNECT SUPPLIERS WITH INGREDIENTS

        String supplierIngredientsTable = """
                CREATE TABLE IF NOT EXISTS supplier_ingredients (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    supplier_id INTEGER NOT NULL,

                    ingredient_id INTEGER NOT NULL,

                    FOREIGN KEY(supplier_id)
                    REFERENCES suppliers(id)
                    ON DELETE CASCADE,

                    FOREIGN KEY(ingredient_id)
                    REFERENCES ingredients(id)
                    ON DELETE CASCADE

                );
                """;



        // MAIN PURCHASE RECORD

        String purchasesTable = """
                CREATE TABLE IF NOT EXISTS purchases (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    supplier_id INTEGER NOT NULL,

                    total_cost REAL NOT NULL DEFAULT 0,

                    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,

                    FOREIGN KEY(supplier_id)
                    REFERENCES suppliers(id)

                );
                """;



        // PURCHASE ITEMS

        String purchaseItemsTable = """
                CREATE TABLE IF NOT EXISTS purchase_items (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    purchase_id INTEGER NOT NULL,

                    ingredient_id INTEGER NOT NULL,

                    quantity REAL NOT NULL,

                    cost REAL NOT NULL,

                    FOREIGN KEY(purchase_id)
                    REFERENCES purchases(id)
                    ON DELETE CASCADE,

                    FOREIGN KEY(ingredient_id)
                    REFERENCES ingredients(id)

                );
                """;



        String recipesTable = """
                CREATE TABLE IF NOT EXISTS recipes (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    name TEXT NOT NULL,

                    description TEXT,

                    selling_price REAL NOT NULL,

                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP

                );
                """;



        String recipeIngredientsTable = """
                CREATE TABLE IF NOT EXISTS recipe_ingredients (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    recipe_id INTEGER NOT NULL,

                    ingredient_id INTEGER NOT NULL,

                    quantity_used REAL NOT NULL,


                    FOREIGN KEY(recipe_id)
                    REFERENCES recipes(id)
                    ON DELETE CASCADE,


                    FOREIGN KEY(ingredient_id)
                    REFERENCES ingredients(id)
                    ON DELETE CASCADE

                );
                """;



        String salesTable = """
                CREATE TABLE IF NOT EXISTS sales (

                    id INTEGER PRIMARY KEY AUTOINCREMENT,

                    recipe_id INTEGER NOT NULL,

                    quantity INTEGER NOT NULL DEFAULT 1,

                    total REAL NOT NULL DEFAULT 0,

                    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,


                    FOREIGN KEY(recipe_id)
                    REFERENCES recipes(id)
                    ON DELETE CASCADE

                );
                """;



        String ingredientIndex = """
                CREATE INDEX IF NOT EXISTS idx_ingredient_name

                ON ingredients(name);
                """;



        String productIndex = """
                CREATE INDEX IF NOT EXISTS idx_product_name

                ON products(name);
                """;



        String salesDateIndex = """
                CREATE INDEX IF NOT EXISTS idx_sales_date

                ON sales(sale_date);
                """;




        try(

                Connection connection =
                        Database.connect();


                Statement statement =
                        connection.createStatement()

        ){


            statement.execute(usersTable);

            statement.execute(productsTable);

            statement.execute(ingredientsTable);

            statement.execute(inventoryHistoryTable);

            statement.execute(activitiesTable);

            statement.execute(suppliersTable);


            // NEW SUPPLIER SYSTEM TABLES

            statement.execute(supplierIngredientsTable);

            statement.execute(purchasesTable);

            statement.execute(purchaseItemsTable);



            statement.execute(recipesTable);

            statement.execute(recipeIngredientsTable);

            statement.execute(salesTable);



            statement.execute(ingredientIndex);

            statement.execute(productIndex);

            statement.execute(salesDateIndex);



            System.out.println(
                    "Database tables created successfully"
            );


        }
        catch(Exception e){

            e.printStackTrace();

        }


    }


}