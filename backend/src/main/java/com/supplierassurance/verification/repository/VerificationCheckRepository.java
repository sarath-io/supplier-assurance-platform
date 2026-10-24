package com.supplierassurance.verification.repository;

import com.supplierassurance.verification.domain.VerificationCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationCheckRepository extends JpaRepository<VerificationCheck, UUID> {
    List<VerificationCheck>
    findAllByVerificationRequest_IdAndVerificationRequest_Supplier_Organization_Id(
            UUID requestId,
            UUID organizationId
    );

    Optional<VerificationCheck>
    findByIdAndVerificationRequest_Supplier_Organization_Id(
            UUID checkId,
            UUID organizationId
    );

    List<VerificationCheck>
    findAllByVerificationRequest_Supplier_Organization_Id(
            UUID organizationId
    );
}
