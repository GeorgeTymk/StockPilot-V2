package com.stockpilot.backend.service;

import com.stockpilot.backend.entity.Ingredient;
import com.stockpilot.backend.entity.Supplier;
import com.stockpilot.backend.entity.SupplierIngredient;
import com.stockpilot.backend.repository.IngredientRepository;
import com.stockpilot.backend.repository.SupplierIngredientRepository;
import com.stockpilot.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierIngredientService {

    private final SupplierIngredientRepository supplierIngredientRepository;
    private final SupplierRepository supplierRepository;
    private final IngredientRepository ingredientRepository;

    public SupplierIngredientService(
            SupplierIngredientRepository supplierIngredientRepository,
            SupplierRepository supplierRepository,
            IngredientRepository ingredientRepository
    ) {
        this.supplierIngredientRepository = supplierIngredientRepository;
        this.supplierRepository = supplierRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<SupplierIngredient> getAll() {
        return supplierIngredientRepository.findAll();
    }

    public List<SupplierIngredient> getBySupplier(Long supplierId) {
        return supplierIngredientRepository.findBySupplierId(supplierId);
    }

    public List<SupplierIngredient> getByIngredient(Long ingredientId) {
        return supplierIngredientRepository.findByIngredientId(ingredientId);
    }

    public SupplierIngredient create(
            Long supplierId,
            Long ingredientId
    ) {

        if (supplierIngredientRepository
                .existsBySupplierIdAndIngredientId(supplierId, ingredientId)) {

            throw new RuntimeException(
                    "This supplier is already linked to this ingredient"
            );
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found")
                );

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() ->
                        new RuntimeException("Ingredient not found")
                );

        SupplierIngredient supplierIngredient =
                new SupplierIngredient(supplier, ingredient);

        return supplierIngredientRepository.save(supplierIngredient);
    }

    public void delete(Long id) {
        if (!supplierIngredientRepository.existsById(id)) {
            throw new RuntimeException(
                    "Supplier-ingredient relationship not found"
            );
        }

        supplierIngredientRepository.deleteById(id);
    }
}
