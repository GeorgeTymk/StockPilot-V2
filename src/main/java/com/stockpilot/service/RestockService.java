package com.stockpilot.service;

import com.stockpilot.model.Purchase;

import java.util.List;

public class RestockService {

    private final PurchaseService purchaseService =
            new PurchaseService();

    public boolean restock(
            int supplierId,
            int ingredientId,
            double quantity,
            double cost
    ) {

        if (supplierId <= 0) {
            System.out.println("Invalid supplier ID");
            return false;
        }

        if (ingredientId <= 0) {
            System.out.println("Invalid ingredient ID");
            return false;
        }

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero");
            return false;
        }

        if (cost < 0) {
            System.out.println("Cost cannot be negative");
            return false;
        }

        try {

            PurchaseService.PurchaseItemRequest item =
                    new PurchaseService.PurchaseItemRequest(
                            ingredientId,
                            quantity,
                            cost
                    );

            Purchase purchase =
                    purchaseService.createPurchase(
                            supplierId,
                            List.of(item)
                    );

            if (purchase != null) {

                System.out.println(
                        "Restock completed through backend"
                );

                System.out.println(
                        "Purchase ID: "
                                + purchase.getId()
                );

                return true;
            }

            System.out.println(
                    "Backend did not create purchase"
            );

            return false;

        } catch (Exception e) {

            System.out.println(
                    "Error creating restock through backend"
            );

            e.printStackTrace();

            return false;
        }
    }
}
