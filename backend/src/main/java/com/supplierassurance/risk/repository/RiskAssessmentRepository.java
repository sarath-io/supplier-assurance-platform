
package com.supplierassurance.risk.repository;

import com.supplierassurance.risk.domain.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, UUID> {

    List<RiskAssessment>
    findAllByVerificationRequest_IdAndVerificationRequest_Supplier_Organization_Id(
            UUID requestId,
            UUID organizationId
    );

    Optional<RiskAssessment>
    findByIdAndVerificationRequest_Supplier_Organization_Id(
            UUID assessmentId,
            UUID organizationId
    );
}
