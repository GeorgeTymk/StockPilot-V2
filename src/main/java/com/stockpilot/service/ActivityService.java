package com.stockpilot.service;

import com.stockpilot.database.Database;
import com.stockpilot.model.Activity;

import java.text.DecimalFormat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityService {

        private final DecimalFormat quantityFormat =
        new DecimalFormat("0.##");

    public List<Activity> getRecentActivities() {

        List<Activity> activities = new ArrayList<>();

        String sql =
                """
                SELECT
                    ingredients.name,
                    inventory_history.movement_type,
                    inventory_history.quantity,
                    inventory_history.created_at

                FROM inventory_history

                JOIN ingredients
                    ON ingredients.id = inventory_history.ingredient_id

                ORDER BY inventory_history.id DESC

                LIMIT 10
                """;

        try (

                Connection connection = Database.connect();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()

        ) {

            while (result.next()) {

                String ingredientName =
                        result.getString("name");

                String movementType =
                        result.getString("movement_type");

                double quantity =
                        result.getDouble("quantity");

                String time =
                        formatTime(
                                result.getString("created_at")
                        );

                String message;

                switch (movementType.toUpperCase()) {

                    case "RESTOCK":

    message =
            "🟢 Restocked "
            + ingredientName
            + " (+"
            + quantityFormat.format(quantity)
            + ")";

    break;

                    case "SALE":

                       message =
        "🔴 Used "
        + ingredientName
        + " (-"
        + quantityFormat.format(quantity)
        + ")";

                        break;

                    default:

                        message =
                                ingredientName
                                + " updated";

                }

                activities.add(

                        new Activity(

                                message,

                                movementType,

                                time

                        )

                );

            }

        }

        catch (Exception e) {

            e.printStackTrace();

        }

                System.out.println(
    "Activities loaded: "
    + activities.size()
);
System.out.println("Activities found: " + activities.size());

for (Activity activity : activities) {
    System.out.println(activity.getMessage());
}
        return activities;



    }



    private String formatTime(String dateTime) {

        try {

            LocalDateTime date =
                    LocalDateTime.parse(
                            dateTime.replace(" ", "T")
                    );

            return date.format(
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy • HH:mm"
                    )
            );

        }

        catch (Exception e) {

            return dateTime;

        }

    }

}