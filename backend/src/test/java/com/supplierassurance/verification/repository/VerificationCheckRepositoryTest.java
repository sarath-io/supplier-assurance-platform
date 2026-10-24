
package com.supplierassurance.verification.repository;

import com.supplierassurance.organization.domain.Organization;
import com.supplierassurance.organization.repository.OrganizationRepository;
import com.supplierassurance.supplier.domain.Supplier;
import com.supplierassurance.supplier.repository.SupplierRepository;
import com.supplierassurance.verification.domain.VerificationCheck;
import com.supplierassurance.verification.domain.VerificationCheckStatus;
import com.supplierassurance.verification.domain.VerificationCheckType;
import com.supplierassurance.verification.domain.VerificationRequest;
import com.supplierassurance.verification.repository.VerificationRequestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VerificationCheckRepositoryTest {

    @Autowired
    private VerificationCheckRepository verificationCheckRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistCheckLifecycleWithinOrganization() {
        Organization organization =
                organizationRepository.saveAndFlush(
                        new Organization("SAP Check Test Organization")
                );

        Supplier supplier = supplierRepository.saveAndFlush(
                new Supplier(
                        organization,
                        "Acme Components",
                        "TEST-CHECK-001",
                        "https://example.com",
                        "contact@example.com",
                        "+91-9000000000",
                        "Bengaluru, India"
                )
        );

        VerificationRequest request =
                verificationRequestRepository.saveAndFlush(
                        new VerificationRequest(supplier)
                );

        VerificationCheck check =
                verificationCheckRepository.saveAndFlush(
                        new VerificationCheck(
                                request,
                                VerificationCheckType.GST_REGISTRATION
                        )
                );

        assertEquals(
                VerificationCheckStatus.PENDING,
                check.getStatus()
        );

        var checkId = check.getId();
        var requestId = request.getId();
        var organizationId = organization.getId();

        // Start the check.
        check.start();
        verificationCheckRepository.saveAndFlush(check);

        // Force the next read to come from the database.
        entityManager.clear();

        VerificationCheck inProgress =
                verificationCheckRepository
                        .findByIdAndVerificationRequest_Supplier_Organization_Id(
                                checkId,
                                organizationId
                        )
                        .orElseThrow();

        assertEquals(
                VerificationCheckStatus.IN_PROGRESS,
                inProgress.getStatus()
        );
        assertNotNull(inProgress.getStartedAt());

        // Record a successful check result.
        inProgress.pass("Registration record found and active");
        verificationCheckRepository.saveAndFlush(inProgress);

        entityManager.clear();

        VerificationCheck completed =
                verificationCheckRepository
                        .findByIdAndVerificationRequest_Supplier_Organization_Id(
                                checkId,
                                organizationId
                        )
                        .orElseThrow();

        assertEquals(
                VerificationCheckStatus.PASSED,
                completed.getStatus()
        );
        assertEquals(
                "Registration record found and active",
                completed.getResult()
        );
        assertNotNull(completed.getCompletedAt());

        // A completed check cannot be started again.
        assertThrows(
                IllegalStateException.class,
                completed::start
        );

        assertEquals(
                1,
                verificationCheckRepository
                        .findAllByVerificationRequest_IdAndVerificationRequest_Supplier_Organization_Id(
                                requestId,
                                organizationId
                        )
                        .size()
        );

        assertEquals(
                1,
                verificationCheckRepository
                        .findAllByVerificationRequest_Supplier_Organization_Id(
                                organizationId
                        )
                        .size()
        );
    }
}
