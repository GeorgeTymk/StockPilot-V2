package com.stockpilot.backend.service;

import com.stockpilot.backend.entity.*;
import com.stockpilot.backend.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;

    public SaleService(
            SaleRepository saleRepository,
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository
    ) {
        this.saleRepository = saleRepository;
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Sale createSale(Long recipeId, Integer quantity) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        List<RecipeIngredient> recipeIngredients =
                recipeIngredientRepository.findByRecipeId(recipeId);

        // Check stock before making any changes
        for (RecipeIngredient recipeIngredient : recipeIngredients) {

            Ingredient ingredient = recipeIngredient.getIngredient();

            BigDecimal required = recipeIngredient.getQuantityUsed()
                    .multiply(BigDecimal.valueOf(quantity));

            if (ingredient.getQuantity().compareTo(required) < 0) {
                throw new RuntimeException(
                        "Insufficient stock for ingredient: "
                                + ingredient.getName()
                );
            }
        }

        // Deduct ingredients
        for (RecipeIngredient recipeIngredient : recipeIngredients) {

            Ingredient ingredient = recipeIngredient.getIngredient();

            BigDecimal required = recipeIngredient.getQuantityUsed()
                    .multiply(BigDecimal.valueOf(quantity));

            ingredient.setQuantity(
                    ingredient.getQuantity().subtract(required)
            );

            ingredientRepository.save(ingredient);
        }

        // Create sale
        Sale sale = new Sale();

        sale.setRecipe(recipe);
        sale.setQuantity(quantity);

        BigDecimal total = recipe.getSellingPrice()
                .multiply(BigDecimal.valueOf(quantity));

        sale.setTotal(total);

        return saleRepository.save(sale);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }
}
