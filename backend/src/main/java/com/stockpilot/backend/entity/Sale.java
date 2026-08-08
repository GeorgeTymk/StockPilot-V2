package com.stockpilot.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private BigDecimal total;

    @Column(name = "sale_date")
    private LocalDateTime saleDate;

    public Sale() {
    this.saleDate = LocalDateTime.now();
}
    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }
}
