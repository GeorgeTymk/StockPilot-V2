package com.stockpilot.backend.controller;

import com.stockpilot.backend.entity.SupplierIngredient;
import com.stockpilot.backend.service.SupplierIngredientService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-ingredients")
@CrossOrigin(origins = "*")
public class SupplierIngredientController {

    private final SupplierIngredientService supplierIngredientService;

    public SupplierIngredientController(
            SupplierIngredientService supplierIngredientService
    ) {
        this.supplierIngredientService = supplierIngredientService;
    }

    @GetMapping
    public List<SupplierIngredient> getAll() {
        return supplierIngredientService.getAll();
    }

    @GetMapping("/supplier/{supplierId}")
    public List<SupplierIngredient> getBySupplier(
            @PathVariable Long supplierId
    ) {
        return supplierIngredientService.getBySupplier(supplierId);
    }

    @GetMapping("/ingredient/{ingredientId}")
    public List<SupplierIngredient> getByIngredient(
            @PathVariable Long ingredientId
    ) {
        return supplierIngredientService.getByIngredient(ingredientId);
    }

    @PostMapping
    public SupplierIngredient create(
            @RequestParam Long supplierId,
            @RequestParam Long ingredientId
    ) {
        return supplierIngredientService.create(
                supplierId,
                ingredientId
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        supplierIngredientService.delete(id);
    }
}
