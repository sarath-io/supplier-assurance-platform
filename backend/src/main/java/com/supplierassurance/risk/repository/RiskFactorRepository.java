
package com.supplierassurance.risk.repository;

import com.supplierassurance.risk.domain.RiskFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiskFactorRepository
        extends JpaRepository<RiskFactor, UUID> {

    List<RiskFactor>
    findAllByRiskAssessment_IdAndRiskAssessment_VerificationRequest_Supplier_Organization_Id(
            UUID assessmentId,
            UUID organizationId
    );

    Optional<RiskFactor>
    findByIdAndRiskAssessment_VerificationRequest_Supplier_Organization_Id(
            UUID factorId,
            UUID organizationId
    );

    List<RiskFactor> findAllByRiskAssessment_Id(UUID assessmentId);
}
