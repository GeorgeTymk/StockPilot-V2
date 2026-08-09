package com.stockpilot.backend.controller;

import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.entity.Recipe;
import com.stockpilot.backend.entity.RecipeIngredient;
import com.stockpilot.backend.repository.IngredientRepository;
import com.stockpilot.backend.repository.RecipeIngredientRepository;
import com.stockpilot.backend.repository.RecipeRepository;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

// =====================================================
// GET INGREDIENTS FOR RECIPE
// GET /api/recipe-ingredients/recipe/{recipeId}
// =====================================================

@GetMapping("/recipe/{recipeId}")
public List<RecipeIngredient> getByRecipe(
        @PathVariable Long recipeId
) {
    return recipeIngredientRepository.findByRecipeId(recipeId);
}

// =====================================================
// ADD INGREDIENT TO RECIPE
// POST /api/recipe-ingredients
// =====================================================

@PostMapping
public RecipeIngredient addIngredient(
        @RequestParam Long recipeId,
        @RequestParam Long ingredientId,
        @RequestParam BigDecimal quantityUsed
) {

    Recipe recipe =
            recipeRepository.findById(recipeId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Recipe not found"
                            )
                    );

    Ingredient ingredient =
            ingredientRepository.findById(ingredientId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Ingredient not found"
                            )
                    );

    RecipeIngredient item =
            new RecipeIngredient();

    item.setRecipe(recipe);
    item.setIngredient(ingredient);
    item.setQuantityUsed(quantityUsed);

    return recipeIngredientRepository.save(item);
}

// =====================================================
// DELETE INGREDIENT FROM RECIPE
// DELETE /api/recipe-ingredients/{id}
// =====================================================

@DeleteMapping("/{id}")
public void deleteIngredient(
        @PathVariable Long id
) {

    if (!recipeIngredientRepository.existsById(id)) {
        throw new RuntimeException(
                "Recipe ingredient not found"
        );
    }

    recipeIngredientRepository.deleteById(id);
}
 

}
