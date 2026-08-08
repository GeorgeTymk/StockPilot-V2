package com.stockpilot.backend.controller;

import com.stockpilot.backend.entity.*;
import com.stockpilot.backend.repository.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-ingredients")
@CrossOrigin(origins = "*")
public class RecipeIngredientController {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeIngredientController(
            RecipeIngredientRepository recipeIngredientRepository,
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository
    ) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("/recipe/{recipeId}")
    public List<RecipeIngredient> getByRecipe(
            @PathVariable Long recipeId
    ) {
        return recipeIngredientRepository.findByRecipeId(recipeId);
    }

    @PostMapping
    public RecipeIngredient addIngredient(
            @RequestParam Long recipeId,
            @RequestParam Long ingredientId,
            @RequestParam java.math.BigDecimal quantityUsed
    ) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        RecipeIngredient item = new RecipeIngredient();
        item.setRecipe(recipe);
        item.setIngredient(ingredient);
        item.setQuantityUsed(quantityUsed);

        return recipeIngredientRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public void deleteIngredient(@PathVariable Long id) {
        recipeIngredientRepository.deleteById(id);
    }
}
