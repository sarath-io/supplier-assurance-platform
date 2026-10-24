
package com.supplierassurance.risk.repository;

import com.supplierassurance.evidence.domain.Evidence;
import com.supplierassurance.evidence.domain.EvidenceSourceType;
import com.supplierassurance.evidence.repository.EvidenceRepository;
import com.supplierassurance.organization.domain.Organization;
import com.supplierassurance.organization.repository.OrganizationRepository;
import com.supplierassurance.risk.domain.RiskAssessment;
import com.supplierassurance.risk.domain.RiskFactor;
import com.supplierassurance.risk.domain.RiskFactorType;
import com.supplierassurance.risk.domain.RiskLevel;
import com.supplierassurance.risk.repository.RiskAssessmentRepository;
import com.supplierassurance.risk.repository.RiskFactorRepository;
import com.supplierassurance.supplier.domain.Supplier;
import com.supplierassurance.supplier.repository.SupplierRepository;
import com.supplierassurance.verification.domain.VerificationCheck;
import com.supplierassurance.verification.domain.VerificationCheckType;
import com.supplierassurance.verification.domain.VerificationRequest;
import com.supplierassurance.verification.repository.VerificationCheckRepository;
import com.supplierassurance.verification.repository.VerificationRequestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RiskAndEvidenceRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Autowired
    private VerificationCheckRepository verificationCheckRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private RiskFactorRepository riskFactorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistEvidenceAssessmentAndRiskFactor() {
        // 1. Create the organization.
        Organization organization =
                organizationRepository.saveAndFlush(
                        new Organization("SAP Risk Test Organization")
                );

        // 2. Create the supplier.
        Supplier supplier = supplierRepository.saveAndFlush(
                new Supplier(
                        organization,
                        "Acme Components",
                        "TEST-RISK-001",
                        "https://example.com",
                        "contact@example.com",
                        "+91-9000000000",
                        "Bengaluru, India"
                )
        );

        // 3. Create a verification request.
        VerificationRequest request =
                verificationRequestRepository.saveAndFlush(
                        new VerificationRequest(supplier)
                );

        // 4. Create and complete an individual check.
        VerificationCheck check =
                new VerificationCheck(
                        request,
                        VerificationCheckType.GST_REGISTRATION
                );

        check.start();
        check.pass("Registration record found and active");

        verificationCheckRepository.saveAndFlush(check);

        // 5. Store source-backed evidence.
        Evidence evidence = new Evidence(
                check,
                EvidenceSourceType.AUTHORIZED_PROVIDER,
                "mock-gst/TEST-RISK-001",
                Instant.now(),
                Map.of(
                        "registrationStatus", "ACTIVE",
                        "provider", "MOCK_GST_PROVIDER"
                )
        );

        Evidence savedEvidence =
                evidenceRepository.saveAndFlush(evidence);

        // 6. Create a versioned risk assessment.
        RiskAssessment assessment = new RiskAssessment(
                request,
                40,
                "risk-v1"
        );

        RiskAssessment savedAssessment =
                riskAssessmentRepository.saveAndFlush(assessment);

        UUID organizationId = organization.getId();
        UUID evidenceId = savedEvidence.getId();
        UUID assessmentId = savedAssessment.getId();

        assertEquals(RiskLevel.MEDIUM, savedAssessment.getRiskLevel());

        // Clear managed entities so subsequent lookups hit the database.
        entityManager.clear();

        // 7. Retrieve and verify the evidence.
        Evidence retrievedEvidence = evidenceRepository
                .findByIdAndVerificationCheck_VerificationRequest_Supplier_Organization_Id(
                        evidenceId,
                        organizationId
                )
                .orElseThrow();

        assertEquals(
                EvidenceSourceType.AUTHORIZED_PROVIDER,
                retrievedEvidence.getSourceType()
        );
        assertEquals(
                "ACTIVE",
                retrievedEvidence.getData().get("registrationStatus")
        );

        // 8. Retrieve the risk assessment.
        RiskAssessment retrievedAssessment =
                riskAssessmentRepository
                        .findByIdAndVerificationRequest_Supplier_Organization_Id(
                                assessmentId,
                                organizationId
                        )
                        .orElseThrow();

        assertEquals(40, retrievedAssessment.getScore());
        assertEquals(RiskLevel.MEDIUM, retrievedAssessment.getRiskLevel());
        assertEquals("risk-v1", retrievedAssessment.getModelVersion());

        // 9. Link an explainable factor to the assessment and evidence.
        RiskFactor factor = new RiskFactor(
                retrievedAssessment,
                retrievedEvidence,
                RiskFactorType.ADDRESS_MISMATCH,
                "Supplier-provided address differs from the recorded address.",
                15
        );

        RiskFactor savedFactor =
                riskFactorRepository.saveAndFlush(factor);

        UUID factorId = savedFactor.getId();

        entityManager.clear();

        // 10. Verify the complete evidence trail.
        RiskFactor retrievedFactor = riskFactorRepository
                .findByIdAndRiskAssessment_VerificationRequest_Supplier_Organization_Id(
                        factorId,
                        organizationId
                )
                .orElseThrow();

        assertEquals(
                RiskFactorType.ADDRESS_MISMATCH,
                retrievedFactor.getFactorType()
        );
        assertEquals(15, retrievedFactor.getImpact());
        assertEquals(
                evidenceId,
                retrievedFactor.getEvidence().getId()
        );
        assertEquals(
                assessmentId,
                retrievedFactor.getRiskAssessment().getId()
        );

        assertEquals(
                1,
                riskFactorRepository
                        .findAllByRiskAssessment_Id(assessmentId)
                        .size()
        );
    }
}
