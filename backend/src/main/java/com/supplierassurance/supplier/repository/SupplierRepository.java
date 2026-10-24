package com.supplierassurance.supplier.repository;

import com.supplierassurance.supplier.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    List<Supplier> findAllByOrganization_Id(UUID organizationId);

    Optional<Supplier> findByIdAndOrganization_Id(
            UUID supplierId,
            UUID organizationId
    );

    boolean existsByOrganization_IdAndRegistrationNumber(
            UUID organizationId,
            String registrationNumber
    );
}
