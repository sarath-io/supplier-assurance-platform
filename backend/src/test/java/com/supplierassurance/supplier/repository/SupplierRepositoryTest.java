package com.supplierassurance.supplier.repository;

import com.supplierassurance.organization.domain.Organization;
import com.supplierassurance.organization.repository.OrganizationRepository;
import com.supplierassurance.supplier.domain.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
public class SupplierRepositoryTest {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void shouldSaveAndRetrieveSupplierWithinOrganization() {
        Organization organization =
                organizationRepository.saveAndFlush(
                        new Organization("SAP Supplier Test Organization")
                );

        Supplier supplier = new Supplier(
                organization,
                "Acme Components",
                "TEST-REG-001",
                "https://example.com",
                "contact@example.com",
                "+91-9000000000",
                "Bengaluru, India"
        );

        Supplier saved = supplierRepository.saveAndFlush(supplier);

        assertNotNull(saved.getId());
        assertEquals("Acme Components", saved.getName());
        assertEquals(
                organization.getId(),
                saved.getOrganization().getId()
        );

        Supplier retrieved = supplierRepository
                .findByIdAndOrganization_Id(
                        saved.getId(),
                        organization.getId()
                )
                .orElseThrow();

        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("TEST-REG-001", retrieved.getRegistrationNumber());

        assertTrue(
                supplierRepository.existsByOrganization_IdAndRegistrationNumber(
                        organization.getId(),
                        "TEST-REG-001"
                )
        );

        assertEquals(
                1,
                supplierRepository
                        .findAllByOrganization_Id(organization.getId())
                        .size()
        );
    }
}
