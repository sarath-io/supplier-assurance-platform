
package com.supplierassurance.evidence.repository;

import com.supplierassurance.evidence.domain.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceRepository
        extends JpaRepository<Evidence, UUID> {

    List<Evidence>
    findAllByVerificationCheck_VerificationRequest_Supplier_Organization_Id(
            UUID organizationId
    );

    Optional<Evidence>
    findByIdAndVerificationCheck_VerificationRequest_Supplier_Organization_Id(
            UUID evidenceId,
            UUID organizationId
    );

    List<Evidence> findAllByVerificationCheck_Id(UUID checkId);
}
