package com.stockpilot.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier_ingredients")
public class SupplierIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public SupplierIngredient() {
    }

    public SupplierIngredient(
            Supplier supplier,
            Ingredient ingredient
    ) {
        this.supplier = supplier;
        this.ingredient = ingredient;
    }

    public Long getId() {
        return id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
