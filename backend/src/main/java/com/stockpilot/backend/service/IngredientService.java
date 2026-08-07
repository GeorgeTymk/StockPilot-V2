package com.stockpilot.backend.service;

import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;


    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }


    public List<Ingredient> getAllIngredients() {

        return ingredientRepository.findAll();

    }


    public Ingredient getIngredientById(Long id) {

        return ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ingredient not found")
                );

    }


    public Ingredient createIngredient(Ingredient ingredient) {

        return ingredientRepository.save(ingredient);

    }


    public Ingredient updateIngredient(Long id, Ingredient updatedIngredient) {

        Ingredient existing = getIngredientById(id);


        existing.setName(updatedIngredient.getName());

        existing.setQuantity(updatedIngredient.getQuantity());

        existing.setUnit(updatedIngredient.getUnit());

        existing.setMinimumStock(updatedIngredient.getMinimumStock());


        return ingredientRepository.save(existing);

    }


    public void deleteIngredient(Long id) {

        ingredientRepository.deleteById(id);

    }

}