package com.stockpilot.backend.repository;

import com.stockpilot.backend.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
