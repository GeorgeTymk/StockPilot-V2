package com.stockpilot.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    public Purchase() {
    }

    public Purchase(
            Supplier supplier,
            BigDecimal totalCost
    ) {
        this.supplier = supplier;
        this.totalCost = totalCost;
    }

    public Long getId() {
        return id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
