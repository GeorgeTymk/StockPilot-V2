package com.stockpilot.backend.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String name;


    @Column(nullable = false)
    private BigDecimal quantity;


    @Column(nullable = false)
    private String unit;


    @Column(name = "minimum_stock", nullable = false)
    private BigDecimal minimumStock;


    // Required by JPA
    public Ingredient() {
    }


    public Ingredient(
            String name,
            BigDecimal quantity,
            String unit,
            BigDecimal minimumStock
    ) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.minimumStock = minimumStock;
    }
    public Long getId(){
    return id;
}


public String getName(){
    return name;
}


public BigDecimal getQuantity(){
    return quantity;
}


public String getUnit(){
    return unit;
}


public BigDecimal getMinimumStock(){
    return minimumStock;
}
public void setName(String name){
    this.name = name;
}


public void setQuantity(BigDecimal quantity){
    this.quantity = quantity;
}


public void setUnit(String unit){
    this.unit = unit;
}


public void setMinimumStock(BigDecimal minimumStock){
    this.minimumStock = minimumStock;
}

}