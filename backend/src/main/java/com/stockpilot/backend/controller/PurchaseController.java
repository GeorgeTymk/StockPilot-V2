package com.stockpilot.backend.controller;

import com.stockpilot.backend.dto.PurchaseRequest;
import com.stockpilot.backend.entity.Purchase;
import com.stockpilot.backend.service.PurchaseService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public Purchase getPurchase(@PathVariable Long id) {
        return purchaseService.getPurchaseById(id);
    }

    @PostMapping
    public Purchase createPurchase(
            @RequestBody PurchaseRequest request
    ) {
        return purchaseService.createPurchase(request);
    }
}
