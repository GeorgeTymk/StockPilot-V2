package com.stockpilot.backend.repository;

import com.stockpilot.backend.entity.SupplierIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierIngredientRepository
        extends JpaRepository<SupplierIngredient, Long> {

    List<SupplierIngredient> findBySupplierId(Long supplierId);

    List<SupplierIngredient> findByIngredientId(Long ingredientId);

    boolean existsBySupplierIdAndIngredientId(
            Long supplierId,
            Long ingredientId
    );
}
