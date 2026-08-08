package com.stockpilot.backend.service;

import com.stockpilot.backend.dto.PurchaseRequest;
import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.entity.Purchase;
import com.stockpilot.backend.entity.PurchaseItem;
import com.stockpilot.backend.entity.Supplier;
import com.stockpilot.backend.repository.IngredientRepository;
import com.stockpilot.backend.repository.PurchaseItemRepository;
import com.stockpilot.backend.repository.PurchaseRepository;
import com.stockpilot.backend.repository.SupplierRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            PurchaseItemRepository purchaseItemRepository,
            SupplierRepository supplierRepository,
            IngredientRepository ingredientRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.supplierRepository = supplierRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Purchase createPurchase(PurchaseRequest request) {

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Purchase must contain at least one item");
        }

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setPurchaseDate(LocalDateTime.now());

        BigDecimal totalCost = BigDecimal.ZERO;

        purchase = purchaseRepository.save(purchase);

        for (PurchaseRequest.Item requestItem : request.getItems()) {

            Ingredient ingredient = ingredientRepository
                    .findById(requestItem.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            BigDecimal quantity = requestItem.getQuantity();
            BigDecimal cost = requestItem.getCost();

            if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Quantity must be greater than zero");
            }

            if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Cost cannot be negative");
            }

            PurchaseItem item = new PurchaseItem(
                    purchase,
                    ingredient,
                    quantity,
                    cost
            );

            purchaseItemRepository.save(item);

            // Automatically increase ingredient stock
            ingredient.setQuantity(
                    ingredient.getQuantity().add(quantity)
            );

            ingredientRepository.save(ingredient);

            totalCost = totalCost.add(cost);
        }

        purchase.setTotalCost(totalCost);

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
    }
}
