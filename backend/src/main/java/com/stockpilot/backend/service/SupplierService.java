package com.stockpilot.backend.service;

import com.stockpilot.backend.entity.Supplier;
import com.stockpilot.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Supplier not found")
                );
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {

        Supplier existing = getSupplierById(id);

        existing.setName(updatedSupplier.getName());
        existing.setPhone(updatedSupplier.getPhone());
        existing.setEmail(updatedSupplier.getEmail());

        return supplierRepository.save(existing);
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}
