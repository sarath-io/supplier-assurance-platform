package com.supplierassurance.verification.repository;

import com.supplierassurance.verification.domain.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, UUID> {
    List<VerificationRequest> findAllBySupplier_IdAndSupplier_Organization_Id(
            UUID supplierId,
            UUID organizationId
    );

    Optional<VerificationRequest> findByIdAndSupplier_Organization_Id(
            UUID requestId,
            UUID organizationId
    );

    List<VerificationRequest> findAllBySupplier_Organization_Id(
            UUID organizationId
    );
}
