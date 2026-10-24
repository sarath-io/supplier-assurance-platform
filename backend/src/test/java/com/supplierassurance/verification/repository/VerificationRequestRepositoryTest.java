package com.supplierassurance.verification.repository;

import com.supplierassurance.organization.domain.Organization;
import com.supplierassurance.organization.repository.OrganizationRepository;
import com.supplierassurance.supplier.domain.Supplier;
import com.supplierassurance.supplier.repository.SupplierRepository;
import com.supplierassurance.verification.domain.VerificationRequest;
import com.supplierassurance.verification.domain.VerificationRequestStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
public class VerificationRequestRepositoryTest {
    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistRequestLifecycleWithinOrganization() {
        Organization organization =
                organizationRepository.saveAndFlush(
                        new Organization("SAP Verification Test Organization")
                );

        Supplier supplier = supplierRepository.saveAndFlush(
                new Supplier(
                        organization,
                        "Acme Components",
                        "TEST-VER-001",
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

        assertEquals(
                VerificationRequestStatus.REQUESTED,
                request.getStatus()
        );
        assertNotNull(request.getRequestedAt());

        // Clear the persistence context to verify database retrieval.
        entityManager.clear();

        VerificationRequest retrieved =
                verificationRequestRepository
                        .findByIdAndSupplier_Organization_Id(
                                request.getId(),
                                organization.getId()
                        )
                        .orElseThrow();

        assertEquals(
                VerificationRequestStatus.REQUESTED,
                retrieved.getStatus()
        );

        // Transition REQUESTED -> IN_PROGRESS.
        retrieved.start();
        verificationRequestRepository.saveAndFlush(retrieved);

        entityManager.clear();

        VerificationRequest inProgress =
                verificationRequestRepository
                        .findByIdAndSupplier_Organization_Id(
                                request.getId(),
                                organization.getId()
                        )
                        .orElseThrow();

        assertEquals(
                VerificationRequestStatus.IN_PROGRESS,
                inProgress.getStatus()
        );
        assertNotNull(inProgress.getStartedAt());

        // Transition IN_PROGRESS -> COMPLETED.
        inProgress.complete();
        verificationRequestRepository.saveAndFlush(inProgress);

        entityManager.clear();

        VerificationRequest completed =
                verificationRequestRepository
                        .findByIdAndSupplier_Organization_Id(
                                request.getId(),
                                organization.getId()
                        )
                        .orElseThrow();

        assertEquals(
                VerificationRequestStatus.COMPLETED,
                completed.getStatus()
        );
        assertNotNull(completed.getCompletedAt());

        // A completed request cannot be started again.
        assertThrows(
                IllegalStateException.class,
                completed::start
        );

        assertEquals(
                1,
                verificationRequestRepository
                        .findAllBySupplier_Organization_Id(
                                organization.getId()
                        )
                        .size()
        );
    }
}
