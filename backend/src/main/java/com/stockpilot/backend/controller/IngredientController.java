package com.stockpilot.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "*")
public class IngredientController {


    private final IngredientService ingredientService;


    public IngredientController(IngredientService ingredientService) {

        this.ingredientService = ingredientService;

    }


    @GetMapping
    public List<Ingredient> getAllIngredients(){

        return ingredientService.getAllIngredients();

    }


    @GetMapping("/{id}")
    public Ingredient getIngredient(@PathVariable Long id){

        return ingredientService.getIngredientById(id);

    }


    @PostMapping
    public Ingredient createIngredient(
            @RequestBody Ingredient ingredient
    ){

        return ingredientService.createIngredient(ingredient);

    }


    @PutMapping("/{id}")
    public Ingredient updateIngredient(
            @PathVariable Long id,
            @RequestBody Ingredient ingredient
    ){

        return ingredientService.updateIngredient(id, ingredient);

    }


    @DeleteMapping("/{id}")
    public void deleteIngredient(
            @PathVariable Long id
    ){

        ingredientService.deleteIngredient(id);

    }

}