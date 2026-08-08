package com.stockpilot.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "purchase_items")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal cost;

    public PurchaseItem() {
    }

    public PurchaseItem(
            Purchase purchase,
            Ingredient ingredient,
            BigDecimal quantity,
            BigDecimal cost
    ) {
        this.purchase = purchase;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
