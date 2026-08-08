package com.stockpilot.backend.controller;

import com.stockpilot.backend.entity.Sale;
import com.stockpilot.backend.service.SaleService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{id}")
    public Sale getSale(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    @PostMapping
    public Sale createSale(@RequestBody Map<String, Object> request) {

        Long recipeId = Long.valueOf(
                request.get("recipeId").toString()
        );

        Integer quantity = Integer.valueOf(
                request.get("quantity").toString()
        );

        return saleService.createSale(recipeId, quantity);
    }
}
